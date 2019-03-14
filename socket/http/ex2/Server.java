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
		serverSocket = new ServerSocket(TCP_PORT);//TCP�׽��ֵĶ���
		executorService = Executors.newFixedThreadPool(POOL_SIZE);//�̳߳صĶ���
		System.out.println("������������");
	}

	public static void main(String[] args) throws IOException {
		new Server().service(); // ��������
	}
	public void service() {
		Socket socket = null;
		while (true) {
			try {
				/**
				 * ʵ��TCP�̵߳Ĵ���
				 */
				socket = serverSocket.accept();
				executorService.execute(new TCPThread(socket));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
