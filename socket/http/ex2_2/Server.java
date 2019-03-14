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
	static ExecutorService executorService;  //线程池
	private final static int POOL_SIZE = 8;
	
	public Server() throws IOException{
		serverSocket = new ServerSocket(port,2);
		serverSocket.setSoTimeout(0);
		//创建线程池
		//Runtime的availableProcessors()方法返回当前系统可用处理器的数目
		//由JVM根据系统的情况来决定线程的数量
		executorService=Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()*POOL_SIZE);
		System.out.println("服务器启动...");
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
