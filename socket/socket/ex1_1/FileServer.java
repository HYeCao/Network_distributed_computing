package ex1_1;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer {
	private final static int UDP_PORT = 2020;
	private final static int TCP_PORT = 2021;
	private final static int POOL_SIZE = 5;

	ServerSocket serverSocket;
	DatagramSocket datagramSocket;
	ExecutorService executorService;
	// static File root;

	public FileServer() throws IOException {
		serverSocket = new ServerSocket(TCP_PORT);//TCP套接字的定义
		datagramSocket = new DatagramSocket(UDP_PORT);//数据报套接字的定义
		executorService = Executors.newFixedThreadPool(POOL_SIZE);//线程池的定义
		// this.root = root;
		System.out.println("服务器启动。");
	}

	public static void main(String[] args) throws IOException {
		new FileServer().service(); // 启动服务
	}

	public void service() {
		Socket socket = null;

		while (true) {
			try {
				/**
				 * 实现TCP以及UDP线程的创建
				 */
				socket = serverSocket.accept();
				executorService.execute(new MyThread(socket));
				Thread work = new Thread(new UThread(datagramSocket));
				work.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
