package com.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class Main {
	public static void main(String[] args) {
		Server server = new Server();
		Thread serverThread = new Thread(server);
		serverThread.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command;
		while (true) {
			command = null;
			try {
				command = br.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			if (command != null && command.toLowerCase().equals("exit"))
				break;
		}
	}
}
