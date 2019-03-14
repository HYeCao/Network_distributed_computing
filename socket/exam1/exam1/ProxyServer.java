package exam1;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class ProxyServer {
	
	private final static int TCP_PORT = 8000;
	private final static int POOL_SIZE = 5;

	ServerSocket serverSocket;
	ExecutorService executorService;

	public ProxyServer() throws IOException {
		serverSocket = new ServerSocket(TCP_PORT);//TCP�׽��ֵĶ���
		executorService = Executors.newFixedThreadPool(POOL_SIZE);//�̳߳صĶ���
		System.out.println("Proxy Server is strating");
	}

	public static void main(String[] args) throws IOException {
		new ProxyServer().service(); // ��������
	}
	public void service() {
		Socket socket = null;
		while (true) {
			try {
				/**
				 * ʵ��TCP�̵߳Ĵ���
				 */
				socket = serverSocket.accept();
				executorService.execute(new MyHandler(socket));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
