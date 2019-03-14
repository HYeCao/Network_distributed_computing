package ex1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import past.ServerThread;

/**
 * tcp connect, server side threads creation
 * 
 * @author Cao Hongye
 *
 */
public class FileServer {
	ServerSocket serverSocket;
	private final int PORT = 2021; // 端口
	static DatagramSocket Usocket; // 客户端DatagramSocket
	ExecutorService executorService; // 线程池
	final int POOL_SIZE = 4; // 单个处理器线程池工作线程数目

	public FileServer() throws IOException {
		serverSocket = new ServerSocket(PORT); // 创建服务器端套接字
		Usocket = new DatagramSocket(2020);// 创建UDP套接字
		serverSocket.setSoTimeout(3000000);// 设置等待时间300s
		// 创建线程池
		// Runtime的availableProcessors()方法返回当前系统可用处理器的数目
		// 由JVM根据系统的情况来决定线程的数量
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		System.out.println("服务器启动。");
	}

	public static void main(String[] args) throws IOException {
		new FileServer().service(); // 启动服务
	}

	/**
	 * service implements 为客户端创建线程。 实现多线程的服务
	 */
	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				Thread work = new Thread(new MyThread(socket, Usocket));
				// 为客户连接创建工作线程
				work.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
