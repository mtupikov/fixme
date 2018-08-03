package com.core;

import com.core.decoders.AcceptConnectionDecoder;
import com.core.decoders.ExecuteOrRejectDecoder;
import com.core.decoders.SellOrBuyDecoder;
import com.core.encoders.AcceptConnectionEncoder;
import com.core.encoders.ExecuteOrRejectEncoder;
import com.core.encoders.SellOrBuyEncoder;
import com.core.exceptions.EmptyInput;
import com.core.exceptions.ErrorInput;
import com.core.messages.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client implements Runnable {
	private String	clientName;
	private int		uniqueID;

	public Client(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void run() {
		String host = "localhost";
		int port = 5000;
		if (clientName.equals("Market"))
			port = 5001;
		System.out.println(port);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(
							new AcceptConnectionEncoder(),
							new SellOrBuyEncoder(),
							new ExecuteOrRejectEncoder(),
							new AcceptConnectionDecoder(),
							new SellOrBuyDecoder(),
							new ExecuteOrRejectDecoder(),
							new ClientHandler());
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	class ClientHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println(clientName + " is connecting to router..");
			MessageAcceptConnection msg = new MessageAcceptConnection(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString(), 0, 0);
			ctx.writeAndFlush(msg);
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			FIXMessage message = (FIXMessage)msg;
			if (message.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {
				MessageAcceptConnection ret = (MessageAcceptConnection)msg;
				uniqueID = ret.getId();
				System.out.println("Connection with router established. ID: " + uniqueID);
			} else if (	message.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
					message.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString())) {
				MessageSellOrBuy ret = (MessageSellOrBuy)msg;

			} else if (	message.getMessageType().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
					message.getMessageType().equals(MessageTypes.MESSAGE_REJECT.toString())) {
				MessageExecuteOrReject ret = (MessageExecuteOrReject)msg;

			}
		}

		private void channelWrite(ChannelHandlerContext ctx) throws Exception {
			try {
				String input = getTextFromUser();
				if (input.length() == 0)
					throw new EmptyInput();
				else if (clientName.equals("Broker"))
					handleBrokerWrite(ctx, input);
			} catch (Exception e) {
				e.printStackTrace();
				channelWrite(ctx);
			}
		}

		private void handleBrokerWrite(ChannelHandlerContext ctx, String s) throws Exception {
			String[] split = s.split(" ");
			if (split.length != 5)
				throw new ErrorInput();
			MessageSellOrBuy out;
			int marketID = checkID(split[1]);
			String instrument = split[2];
			int quantity = Integer.valueOf(split[3]);
			int price = Integer.valueOf(split[4]);
			if (split[0].toLowerCase().equals("sell")) {
				out = new MessageSellOrBuy(MessageTypes.MESSAGE_SELL.toString(), marketID, uniqueID, instrument, quantity, price);
			} else if (split[0].toLowerCase().equals("buy")) {
				out = new MessageSellOrBuy(MessageTypes.MESSAGE_BUY.toString(), marketID, uniqueID, instrument, quantity, price);
			} else
				throw new ErrorInput();
			ctx.writeAndFlush(out);
			System.out.println("Sending request to router..");
		}

		private int checkID(String id) throws Exception {
			int iID = Integer.valueOf(id);
			if (id.length() != 6)
				throw new ErrorInput();
			return iID;
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			channelWrite(ctx);
		}

		private String getTextFromUser() throws Exception {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		}
	}
}

