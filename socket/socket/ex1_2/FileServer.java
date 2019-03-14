package ex1_2;

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
	File root;

	public FileServer(File root) throws IOException {
		serverSocket = new ServerSocket(TCP_PORT);
		datagramSocket = new DatagramSocket(UDP_PORT);
		executorService = Executors.newFixedThreadPool(POOL_SIZE);
		this.root = root;
		System.out.println("the service is starting");
	}
	
	public void service() {
		Socket socket = null;
		new Thread(new UdpHandler(datagramSocket)).start();
		while (true) {
			try {
				socket = serverSocket.accept();
				executorService.execute(new TcpHandler(root, socket));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
