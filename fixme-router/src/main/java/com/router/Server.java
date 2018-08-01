package com.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server implements Runnable {

	class FIXattachment {
		AsynchronousServerSocketChannel	server;
		AsynchronousSocketChannel		client;
		ByteBuffer						fixMessage;
		SocketAddress					clientAddress;
		boolean							isRead;
	}

	@Override
	public void run() {
		try {
			AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
			AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group);
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5000);
			serverChannel.bind(hostAddress);
			FIXattachment newFIX = new FIXattachment();
			newFIX.server = serverChannel;
			serverChannel.accept(newFIX, new ConnectionHandler());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, FIXattachment> {
		@Override
		public void completed(AsynchronousSocketChannel client, FIXattachment attachment) {
			try {
				SocketAddress clientAddr = client.getRemoteAddress();
				System.out.println("Accepted a connection from " + clientAddr);
				attachment.server.accept(attachment, this);
				ReadWriteHandler handler = new ReadWriteHandler();
				FIXattachment fix = new FIXattachment();
				fix.server = attachment.server;
				fix.client = client;
				fix.fixMessage = ByteBuffer.allocate(2048);
				fix.isRead = true;
				fix.clientAddress = clientAddr;
				client.read(fix.fixMessage, fix, handler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void failed(Throwable exc, FIXattachment attachment) {
			exc.printStackTrace();
		}
	}

	class ReadWriteHandler implements CompletionHandler<Integer, FIXattachment> {
		@Override
		public void completed (Integer result, FIXattachment attachment) {
			System.out.println(attachment.isRead);
			if (attachment.isRead) {
				String message = new String(attachment.fixMessage.array());
				message = message.trim();
				System.out.println("Client " + attachment.clientAddress + " says: " + message);
				attachment.isRead = false;
				attachment.fixMessage.rewind();
			} else {
				attachment.client.write(attachment.fixMessage, attachment, this);
				attachment.isRead = true;
				attachment.fixMessage.clear();
				attachment.client.read(attachment.fixMessage, attachment, this);
			}
		}

		@Override
		public void failed(Throwable exc, FIXattachment attachment) {
			exc.printStackTrace();
		}
	}
}
