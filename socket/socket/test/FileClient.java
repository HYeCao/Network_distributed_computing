package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * tcp echo test, client side
 * 
 * @author wben
 *
 */
public class FileClient {
	static final int PORT = 2021; //连接端口
	static final String HOST = "127.0.0.1"; //连接地址
	Socket socket = new Socket();
 
	public FileClient() throws UnknownHostException, IOException {
		//socket = new Socket(HOST, PORT); //创建客户端套接字
		socket = new Socket();
		socket.connect(new InetSocketAddress(HOST, PORT));
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		new FileClient().send();
	}

	/**
	 * send implements
	 */
	public void send() {
		try {
			//客户端输出流，向服务器发消息
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			//客户端输入流，接收服务器消息
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter pw = new PrintWriter(bw, true); //装饰输出流，及时刷新
			Scanner in = new Scanner(System.in); //接受用户信息
			String msg = null;
			
			System.out.println( socket.getInetAddress()+":"+socket.getPort()+"> 连接成功");
			while ((msg = in.nextLine()) != null) {
				pw.println(msg); //发送给服务器端
				
				if(br.readLine().equals(" "))
				System.out.println(br.readLine()); //输出服务器返回的消息
				if (msg.equals("quit")) {
					break; //退出
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close(); //断开连接
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	 public static void print(File f){
//	        if(f!=null){
//	            if(f.isDirectory()){
//	                File[] fileArray=f.listFiles();
//	                if(fileArray!=null){
//	                    for (int i = 0; i < fileArray.length; i++) {
//	                        //递归调用
//	                        print(fileArray[i]);
//	                    }
//	                }
//	            }
//	            else{
////	            	pw.println("your address:" + f);
//	                System.out.println(f);
//	            }
//	        }
//	 }
}
