package com.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Server implements Runnable {
	private AsynchronousServerSocketChannel	serverChannel = null;
	private AsynchronousSocketChannel		clientChannel = null;

	@Override
	public void run() {
		try {
			serverChannel = AsynchronousServerSocketChannel.open();
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5000);
			serverChannel.bind(hostAddress);
			while (true) {
				serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel,Object>() {
							@Override
							public void completed (AsynchronousSocketChannel result, Object attachment) {
								if (serverChannel.isOpen()){
									serverChannel.accept(null, this);
								}
								clientChannel = result;
								if ((clientChannel != null) && (clientChannel.isOpen())) {
									ReadWriteHandler handler = new ReadWriteHandler();
									ByteBuffer buffer = ByteBuffer.allocate(32);
									String kek = "kek";
									clientChannel.read(buffer, kek, handler);
								}
							}
							@Override
							public void failed(Throwable exc, Object attachment) {
								// process error
							}
						});
				System.in.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ReadWriteHandler implements CompletionHandler<Integer, String> {

		@Override
		public void completed (Integer result, String attachment) {
			ByteBuffer buffer = ByteBuffer.allocate(32);
			clientChannel.write(buffer, "lol", this);
		}

		@Override
		public void failed(Throwable exc, String attachment) {
			//
		}
	}

	void closeChannels() {
		try {
			if (clientChannel != null)
				clientChannel.close();
			if (serverChannel != null)
				serverChannel.close();
			clientChannel = null;
			serverChannel = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
