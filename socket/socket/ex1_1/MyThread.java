package ex1_1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import past.ServerThread;

/**
 * java多线程
 * 
 * @author Cao Hongye
 *
 */
public class MyThread implements Runnable { // 负责与单个客户通信的线程
	// 全局变量的初始定义
	private Socket socket;
	private DatagramSocket Usocket;
	private String info;
	BufferedReader br;
	BufferedWriter bw;
	PrintWriter pw;
	File folder;
	private final String root = "D:/新建文件夹/新建文件夹/java";
	private String currentPath = root;
	static int UDPport = 2020;

	/**
	 * 函数初始定义
	 * 
	 * @param socket
	 */
	public MyThread(Socket socket) {
		this.socket = socket;
		// this.info=info;
		folder = new File(root);
	}

	/***
	 * 输入输出流的初始化定义
	 * 
	 * @throws IOException
	 */
	public void initStream() throws IOException { // 初始化输入输出流对象方法
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		pw = new PrintWriter(bw, true);
	}

	/**
	 * 功能：run方法的编写，实现读取客户端信息，进行相应操作，并返回其对应信息 具体的功能实现通过调用相应函数实现
	 */
	public void run() { // 执行的内容
		try {
			initStream(); // 初始化输入输出流对象
			boolean connection = true;

			String info1 = info;
			while (connection) {
				if ((info = br.readLine()) == null) {
					info = info1;
				}
				System.out.println(info);
				if (info.equals("bye")) { // 如果用户输入“bye”就退出
					break;
				} else if (info.equals("ls")) {
					ls();
				}

				else if (info.equals("cd..")) {
					cdback();
				} else {
					StringTokenizer stringTokenizer = new StringTokenizer(info, " ");
					String first = stringTokenizer.nextToken(); // 解析出第一部分

					if (first.equals("cd")) {
						String Path = stringTokenizer.nextToken(); // 解析出第二部分
						cd(Path);
					} else if (first.equals("get")) {

						String getPath = stringTokenizer.nextToken();
						// System.out.println("begin transfor\0");
						get(getPath);
					} else {
						pw.println("unknown cmd \n\0");// 返回用户发送的消息
					}

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 编写cdback方法，实现返回上一目录的功能 参数：空 返回值：空 实现：通过修改全局变量path的地址信息， 实现修改当前地址并返回上一级
	 * 待修改：需判别是否到达根目录，加一个根目录的判断
	 */

	private void cdback() { // 响应cd..
		if (currentPath.equals(root + "/") || currentPath.equals(root)) { // 如果是根目录，则无法继续向上
			pw.println("this is root catalog " + currentPath + "\n\0");
		} else { // 如果不是根目录，则向上返回
			String OldPath = currentPath;
			StringTokenizer stnr = new StringTokenizer(OldPath, "/"); // 用StringTokenizer以/为分界对路径进行切割
			String Path = ""; // Path记录上级目录
			int countTokens = stnr.countTokens(); // 计算共有几个切割结果
			for (int i = 0; i < countTokens - 1; i++) { // 保留除去最后一项的路径，即可得到当前目录的上层目录
				Path = Path + stnr.nextToken() + "/";
			}
			currentPath = Path; // 更新当前目录
			folder = new File(currentPath);// 更新当前目录
			pw.println(currentPath + " > OK\n\0"); // 向客户端发送成功返回上层目录的信息

		}

	}

	/**
	 * 编写ls方法，实现查询出当前目录的操作 参数：空 返回值：空
	 * 实现：通过读取全局变量folder的地址信息，实现当前目录的循环并返回其相应的信息给客户端
	 */
	private void ls() {
		String allFile = new String();
		for (File listOfFile : folder.listFiles()) { // 文件的目录循环
			if (listOfFile.isDirectory()) { // 判别该路径是否正确，是否有目录
				long size = getDirSize(listOfFile, 0); // 获取每个文件的大小
				allFile += "<dir>  " + listOfFile.getName() + "  " + size / 1024 + "KB\n";// 存储获取的文件信息与allfile字符串中
			} else if (listOfFile.isFile()) {
				// 判断该路径下是文件还是目录
				long size = listOfFile.length();
				allFile += "<File>  " + listOfFile.getName() + "  " + size / 1024 + "KB\n";
			}
		}
		// 添加标识符，以确定客户端输出循环终止条件
		allFile += "\0";
		pw.println(allFile);// 输入信息流至客户端

	}

	/**
	 ** 功能：编写cd方法，实现查找目录的功能 实现：对于输入的地址信息首先判别是否在当前目录下，在判别后通过给定的目录信息进入该文件目录中
	 * （修改当前的目录地址folder) 注意： 1.判别所给文件的信息是否错误或者不存在当前目录下 2.判别给定地址为目录文件夹还是文件的类型
	 * 3.修改当前的目录地址folder实现地址的修改，呼应cd..返回上一级的功能
	 * 
	 * @param path:
	 *            用户输入的文件路径信息
	 * 
	 */
	private void cd(String path) {
		String allfile = new String();
		int temp = 0;
		for (File listFile : folder.listFiles()) { // 文件的目录循环
			if (listFile.isDirectory()) { // 判别该路径是否正确，是否有目录
				if (listFile.getName().equals(path)) {
					/**
					 * 这里判断有点问题
					 */
					currentPath = currentPath + "/" + path;
					// pw.println(currentPath);
					folder = new File(currentPath);
					temp++;
					pw.println(path + " >" + " OK\n\0");
					break;
				} else if (listFile.isFile()) {
					if (listFile.getName().equals(path))
						;
					currentPath = currentPath + "/" + path;
					// pw.println(currentPath);
					folder = new File(currentPath);
					long size = getDirSize(listFile, 0);
					pw.println("<File>  " + listFile.getName() + "  " + size / 1024 + "KB\n\0");
					temp++;
					break;
				}
			}

		}
		if (temp == 0) {
			pw.println("unknown dir \n\0");
		}
	}

	/**
	 * 功能：编写get方法，实现UDP数据传送 参数： 返回值： 实现：通过读取get后的文件信息，判别文件的位置，实现文件的下载并传输至客户端
	 * 注意：1.对于UDP的理解
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void get(String path) throws IOException, InterruptedException {

		String allfile = new String();
		int temp = 0;

		for (File listFile : folder.listFiles()) { // 文件的目录循环
			if (listFile.isFile()) {
				if (listFile.getName().equals(path)) {

					pw.println("begin transfor");
					pw.println(currentPath + "/" + path + "\n\0");
					temp++;
					// download(currentPath + "/" + path);

					break;
				}
			}
		}
		if (temp == 0) {
			pw.println("unknow file\n\0");
		}

	}

	/*
	 * 功能：获取指定文件的大小
	 * 
	 * @param folder:当前文件夹
	 * 
	 * @param size:记录文件的大小 return : 每个文件的大小 实现：通过读取特定文件的地址，获取该文件的大小，并且返回给调用的函数
	 */
	public long getDirSize(File folder, long size) {
		for (File FileList : folder.listFiles()) {
			if (FileList.isFile()) {
				size += FileList.length();
			} else if (FileList.isDirectory()) {
				size += getDirSize(FileList, size);
			}
		}
		return size;
	}

}