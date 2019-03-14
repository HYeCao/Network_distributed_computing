package ex2_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	/**
	 * default HTTP port is port 80
	 */
	private static int port = 80;

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * The end of line character sequence.
	 */
	private static String CRLF = "\r\n";

	/**
	 * Input is taken from the keyboard
	 */
	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(
			System.in));

	/**
	 * Output is written to the screen (standard out)
	 */
	static PrintWriter screen = new PrintWriter(System.out, true);
	
	static ServerSocket serverSocket;
	static ExecutorService executorService;  //�̳߳�
	private final static int POOL_SIZE = 8;
	
	public Server() throws IOException{
		serverSocket = new ServerSocket(port,2);
		serverSocket.setSoTimeout(0);
		//�����̳߳�
		//Runtime��availableProcessors()�������ص�ǰϵͳ���ô���������Ŀ
		//��JVM����ϵͳ������������̵߳�����
		executorService=Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()*POOL_SIZE);
		System.out.println("����������...");
	}
	
	public void service(){
		Socket socket = null;
		while(true){
			try{
				
				socket = serverSocket.accept();
				//System.out.println("done");
				executorService.execute(new HttpServer(socket));
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Server().service();
	}
}
