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
	private final int PORT = 2021; // �˿�
	static DatagramSocket Usocket; // �ͻ���DatagramSocket
	ExecutorService executorService; // �̳߳�
	final int POOL_SIZE = 4; // �����������̳߳ع����߳���Ŀ

	public FileServer() throws IOException {
		serverSocket = new ServerSocket(PORT); // �������������׽���
		Usocket = new DatagramSocket(2020);// ����UDP�׽���
		serverSocket.setSoTimeout(3000000);// ���õȴ�ʱ��300s
		// �����̳߳�
		// Runtime��availableProcessors()�������ص�ǰϵͳ���ô���������Ŀ
		// ��JVM����ϵͳ������������̵߳�����
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		System.out.println("������������");
	}

	public static void main(String[] args) throws IOException {
		new FileServer().service(); // ��������
	}

	/**
	 * service implements Ϊ�ͻ��˴����̡߳� ʵ�ֶ��̵߳ķ���
	 */
	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				Thread work = new Thread(new MyThread(socket, Usocket));
				// Ϊ�ͻ����Ӵ��������߳�
				work.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
