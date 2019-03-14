package ex1;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import src.ssd8.socket.udp.echo.EchoClient;

/**
 * tcp echo test, client side udp transfer ，client side
 * 
 * @author Cao Hongye
 *
 */
public class FileClient {
	/**
	 * 全局变量的定义
	 */
	static final int PORT = 2021; // 连接端口
	static final String HOST = "127.0.0.1"; // 连接地址
	static Socket socket = new Socket();
	int remotePort = 2020; // UDP服务器端口
	static String remoteIp = "127.0.0.1"; // 服务器IP
	static DatagramSocket Usocket; // 客户端DatagramSocket
	static PrintWriter pw;
	static BufferedReader br;
/**
 * 功能： 将TCP的连接以及命令的输入整合为一个main函数之中，实现基础命令的功能
 * 
 * UDP开启的问题，需要转到服务器端进行创建，（FileServer)
 * @param args
 * @throws UnknownHostException
 * @throws IOException
 */
	public static void main(String[] args) throws UnknownHostException, IOException {

		socket = new Socket();
		socket.connect(new InetSocketAddress(HOST, PORT));
		String path = "D:/";// 默认保存目录
//		int port = 2020;// UDP端口号
//		Usocket = new DatagramSocket(port); // 服务端DatagramSocket启动

		try {
			// 客户端输出流，向服务器发消息
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			// 客户端输入流，接收服务器消息
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(bw, true); // 装饰输出流，及时刷新
			Scanner in = new Scanner(System.in); // 接受用户信息
			String msg = null;

			System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "> 连接成功");
			while ((msg = in.nextLine()) != null) {
				StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
				String first = stringTokenizer.nextToken(); // 解析出第一部分
				if (first.equals("bye")) {
					
					pw.println("bye");// 结束标志
					Usocket.close();
					socket.close();
					break;
				}
				pw.println(msg); // 发送给服务器端
				String msg1 = br.readLine();// 按行读取服务器端的数据

				while (!msg1.equals("\0")) {// 读取结束标志

					System.out.println(msg1);

					if (msg1.equals("begin transfor")) {// get命令传输的语句
						UDPget(msg);
					}
					msg1 = br.readLine();

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close(); // 断开连接
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/***
	 * 功能:UDP的接收文件实现
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	private static void UDPget(String fileName) throws IOException {

		String path = "D:/";// 默认保存目录
		int port = 2020;// UDP端口号
		StringTokenizer stringTokenizer = new StringTokenizer(fileName, " ");
		String first = stringTokenizer.nextToken(); // 解析出第一部分
		String fn = stringTokenizer.nextToken();// 解析出文件名
		byte[] receiveData = new byte[8192];// 缓存数组
		int buffLen = receiveData.length;// 缓存大小

		DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path + fn)));
		/*
		 * 下方为循环接收文件的实现 方法： 通过设置单一变量i实现循环的接收，同时设置文件输入流，将读取数据写入文件之中
		 * 注意点：最后记得关闭文件的流和socket
		 */
		int i = 0;		
			Usocket = new DatagramSocket(); 	
			SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 2020);
			DatagramPacket dp=new DatagramPacket("".getBytes(),"".getBytes().length,socketAddress);
			Usocket.send(dp);
			
		while (i == 0) {
			// 接收客户端信息
			byte[] receiveByte = new byte[1024];
			
		
			DatagramPacket dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
			Usocket.receive(dataPacket);
			i = dataPacket.getLength();
			// System.out.println(i);//判断接收的长度
			// 接收数据
			if (i > 0) {
				// 根据数据长度循环接收数据
				fileOut.write(receiveByte, 0, i);
				fileOut.flush();
				i = 0;// 循环接收条件的实现
			}
			if (new String(dataPacket.getData(), 0, dataPacket.getLength()).equals("\n")) {
				System.out.println("File received");
				fileOut.close();
				Usocket.close();
				break;
			}

		}
	}

}
