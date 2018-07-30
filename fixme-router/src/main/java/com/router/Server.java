package com.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server implements Runnable {
	private AsynchronousServerSocketChannel	serverChannel = null;
	private AsynchronousSocketChannel		clientChannel = null;

	@Override
	public void run() {
		try {
			serverChannel = AsynchronousServerSocketChannel.open();
			InetSocketAddress hostBrokerAddress = new InetSocketAddress("localhost", 5000);
			InetSocketAddress hostMarketAddress = new InetSocketAddress("localhost", 5001);
			serverChannel.bind(hostBrokerAddress);
			//serverChannel.bind(hostMarketAddress);
			Future acceptResult = serverChannel.accept();
			clientChannel = (AsynchronousSocketChannel) acceptResult.get();
			if (clientChannel != null && clientChannel.isOpen())
				while (true) {
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					Future result = clientChannel.read(buffer);
					while (!result.isDone()) {}
					buffer.flip();
					String message = new String(buffer.array()).trim();
					System.out.println(message);
					buffer.clear();
					if (message.toLowerCase().equals("exit"))
						break;
				}
			else
				System.err.println("Error when creating client channel");
			closeChannels();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
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
