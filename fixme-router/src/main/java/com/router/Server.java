package com.router;

import com.core.messages.*;
import com.core.Decoder;
import com.core.Encoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;

public class Server implements Runnable {
	private static HashMap<Integer, ChannelHandlerContext>	routingTable = new HashMap<>();
	static final int										MARKET_SERVER = 5001;
	static final int										BROKER_SERVER = 5000;
	private EventLoopGroup									bossGroup;
	private EventLoopGroup									workerGroup;
	private int												serverType;

	Server(int serverType) {
		this.serverType = serverType;
	}

	@Override
	public void run() {
		createServer(serverType);
	}

	private String brokerOrMarketString() {
		return serverType == MARKET_SERVER ? "market" : "broker";
	}

	private void createServer(int port) {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new Decoder(),
									new Encoder(),
									new ProcessingHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			shutDown();
		}
	}

	void shutDown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	class ProcessingHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			FIXMessage message = (FIXMessage)msg;
			if (message.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {
				MessageAcceptConnection ret  = new MessageAcceptConnection(message);
				ret.setId(69);
				ctx.writeAndFlush(ret);
				System.out.println("Accepted a connection from " + brokerOrMarketString() + ": " + ctx.channel().remoteAddress());
			} else if (	message.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
					message.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString())) {

			} else if (	message.getMessageType().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
					message.getMessageType().equals(MessageTypes.MESSAGE_REJECT.toString())) {

			}
		}
	}
}
