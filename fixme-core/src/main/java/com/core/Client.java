package com.core;

import com.core.messages.FIXMessage;
import com.core.messages.MessageAcceptConnection;
import com.core.messages.MessageTypes;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client implements Runnable {
	private String clientName;

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
					ch.pipeline().addLast(new Encoder(),
					new Decoder(), new ClientHandler());
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
			FIXMessage msg = new MessageAcceptConnection(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString(), 0, 0);
			ctx.writeAndFlush(msg);
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			FIXMessage message = (FIXMessage) msg;
			if (message.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {
				MessageAcceptConnection ret = new MessageAcceptConnection(message);
				System.out.println("Connection with router established. ID: " + ret.getId());
			} else if (	message.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
					message.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString())) {

			} else if (	message.getMessageType().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
					message.getMessageType().equals(MessageTypes.MESSAGE_REJECT.toString())) {

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

