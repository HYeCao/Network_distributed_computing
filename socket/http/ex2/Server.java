package ex2;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ex1.MyThread;
import ex1_1.UThread;

public class Server {
	
	private final static int TCP_PORT = 80;
	private final static int POOL_SIZE = 5;

	ServerSocket serverSocket;
	ExecutorService executorService;

	public Server() throws IOException {
		serverSocket = new ServerSocket(TCP_PORT);//TCP套接字的定义
		executorService = Executors.newFixedThreadPool(POOL_SIZE);//线程池的定义
		System.out.println("服务器启动。");
	}

	public static void main(String[] args) throws IOException {
		new Server().service(); // 启动服务
	}
	public void service() {
		Socket socket = null;
		while (true) {
			try {
				/**
				 * 实现TCP线程的创建
				 */
				socket = serverSocket.accept();
				executorService.execute(new TCPThread(socket));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
