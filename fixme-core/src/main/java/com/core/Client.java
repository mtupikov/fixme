package com.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client implements Runnable {
	private AsynchronousSocketChannel client;
	private String clientName;

	public Client(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void run() {
		try {
			client = AsynchronousSocketChannel.open();
			int port;
			if (clientName.equals("Market"))
				port = 5001;
			else
				port = 5000;
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
			Future future = client.connect(hostAddress);
			future.get();
			System.out.println(clientName + " client has started: " + client.isOpen());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String message;
			while (true) {
				message = null;
				try {
					message = br.readLine();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
				if (message != null) {
					if (message.toLowerCase().equals("exit"))
						break;
					sendMessage(message);
				}
			}
			closeChannel();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String message) {
		byte[] sendMessage = message.getBytes();
		ByteBuffer sendBuffer = ByteBuffer.wrap(sendMessage);
		Future result = client.write(sendBuffer);
		while (!result.isDone()) {}
		sendBuffer.clear();
	}

	private void closeChannel() {
		try {
			if (client != null)
				client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
