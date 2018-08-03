package com.core;

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
					ch.pipeline().addLast(new ResponseEncoder(),
					new RequestDecoder(), new ClientHandler());
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
			FIXMessage msg = new FIXMessage("lol", 12, "kek", 22, 21, "dsa");
			System.out.println("Enter message type: ");
			msg.setMessageType(getTextFromUser());
			ctx.writeAndFlush(msg);
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println(msg);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			channelActive(ctx);
		}

		private String getTextFromUser() throws Exception {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		}
	}
}

