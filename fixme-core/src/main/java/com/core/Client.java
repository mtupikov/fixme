package com.core;

import com.core.decoders.AcceptConnectionDecoder;
import com.core.decoders.ExecuteOrRejectDecoder;
import com.core.decoders.SellOrBuyDecoder;
import com.core.encoders.AcceptConnectionEncoder;
import com.core.encoders.ExecuteOrRejectEncoder;
import com.core.encoders.SellOrBuyEncoder;
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

