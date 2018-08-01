package com.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client implements Runnable {
	private String clientName;

	class FIXattachment {
		AsynchronousSocketChannel		client;
		ByteBuffer						fixMessage;
		boolean							isRead;
	}

	public Client(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public void run() {
		try {
			AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
			int port;
			if (clientName.equals("Market"))
				port = 5001;
			else
				port = 5000;
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
			Future future = client.connect(hostAddress);
			future.get();
			System.out.println(clientName + " client has started: " + client.isOpen());
			FIXattachment newFIX = new FIXattachment();
			newFIX.client = client;
			newFIX.fixMessage = ByteBuffer.allocate(2048);
			newFIX.isRead = false;
			newFIX.fixMessage.put("Accept me plz".getBytes());
			newFIX.fixMessage.flip();
			ReadWriteHandler readWriteHandler = new ReadWriteHandler();
			client.write(newFIX.fixMessage, newFIX, readWriteHandler);
			Thread.currentThread().join();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	class ReadWriteHandler implements CompletionHandler<Integer, FIXattachment> {
		@Override
		public void completed(Integer result, FIXattachment attachment) {
			System.out.println(attachment.isRead);
			if (attachment.isRead) {
				attachment.fixMessage.flip();
				String message = new String(attachment.fixMessage.array());
				message = message.trim();
				System.out.println("Server responded: " + message);
			} else {
				String message = null;
				try {
					message = getMessage();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (message != null) {
					if (message.toLowerCase().equals("exit"))
						Thread.currentThread().interrupt();
					attachment.isRead = true;
					attachment.fixMessage.clear();
					attachment.fixMessage.put(message.getBytes());
					attachment.client.write(attachment.fixMessage, attachment, this);
				}
			}
		}

		@Override
		public void failed(Throwable exc, FIXattachment attachment) {
			exc.printStackTrace();
		}
	}

	private String getMessage() throws IOException {
		System.out.println("Enter a message: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		return br.readLine();
	}
}
