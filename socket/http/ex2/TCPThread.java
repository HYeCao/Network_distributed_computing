package ex2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream.PutField;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.naming.directory.InvalidSearchFilterException;
import javax.print.DocFlavor.STRING;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.xml.crypto.Data;


public class TCPThread implements Runnable {
	/**
	 * 接收传入的Socket，处理会话
	 */
	private Socket socket;
	/**
	 * 定义输出流，用户获取套接字中信息
	 */
	BufferedOutputStream ostream = null;
	/**
	 * 定义输入流，用于写入套接字信息
	 */
	BufferedInputStream istream = null;
	/**
	 * 定义缓存变量
	 */
	private byte[] buffer;
	/**
	 * 设定指定的缓存大小
	 */
	private static int buffer_size = 8192;
	/**
	 * 头部的定义
	 */
	private StringBuffer header = null;
	/**
	 * 自定义的格式
	 */
	private String CRLF = "\r\n";
	/**
	 * 用于解析的请求报文
	 */
	private String request;
	/**
	 * 指定获取的路径
	 */
	private static String PATH = "";
	/**
	 * 定义的状态量，用户保存当前状态
	 */
	private String status;
	/**
	 * 存放根目录的定义
	 */
	private static String root;

	public TCPThread(Socket socket) throws IOException {

		this.socket = socket;
		/**
		 * 初始化流以及地址
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());
		istream = new BufferedInputStream(socket.getInputStream());
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		this.root = "D:\\images";
	}

	/*
	 * 
	 * get：注意run结构以及get中的代码中对于输入错误信息的处理 对于put方法尝试自己编写 (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			processResponse();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		request = header.toString();
		System.out.println(request);
		System.out.println("transfered Header");

		if (request.startsWith("GET")) {
			try {
				get();//执行get命令
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		} else if (request.startsWith("PUT")) {
			try {
				put();//执行put命令
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			respond400();//响应400 bad request 
		}
	}

	/**
	 * 对于首部的文字进行结构的组织（首部的封装）
	 * @throws Exception
	 */
	public void processResponse() throws Exception {
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
		while (inHeader && ((c = istream.read()) != -1)) {
			switch (c) {
			case '\r':
				break;
			case '\n':
				if (c == last) {
					inHeader = false;
					break;
				}
				last = c;
				header.append("\n");
				break;
			default:
				last = c;
				header.append((char) c);
			}
		}
	}

	/**
	 * put命令的实现
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void put() throws UnsupportedEncodingException, IOException {

		String[] str1 = request.split("\n");//拆分string字符
		long length = 0;
		for (int i = 0; i < str1.length; i++) {
			if (str1[i].startsWith("Content-length")) {
				length = Long.valueOf(str1[i].split(" ")[1]);
			}
		}
		/**
		 * 以下代码实现对于目录的创建以及文件的创建
		 */
		String[] str3 = str1[0].split(" ");
		String[] str4 = str3[1].split("/");
		int n = str4.length;
		// System.out.println(n);//=2则为文件名
		String fileName = root + str4[0];//获取文件名
		File file = null;
//		System.out.println(fileName);//D:\images
		if (n > 2) {
			for (int m = 1; m < n - 1; m++) {
				file = new File(fileName + "\\" + str4[m]);

				if (!file.exists()) {// 如果文件夹不存在

					file.mkdir();// 创建文件夹

				}
				fileName = fileName + "\\" + str4[m];
			}
			fileName = fileName + "\\" + str4[n - 1];
			file = new File(fileName);//创建文件
		    System.out.println(fileName);//输出文件名
		}
		else {
			fileName=fileName+"\\"+str4[1];
			file = new File(fileName);//创建文件
			System.out.println(fileName);//输出文件名
		}
		FileOutputStream outfile = null;//文件流的定义
		Boolean isExist;
		
		if (file.exists()) {
			isExist = true;
		} else {
			isExist = false;
		}
		try {
			outfile = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String data = null;
		try {
			data = getData(length);//获取文件数据
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str2 = "";
		outfile.write(data.getBytes("iso-8859-1"));// 设定的特定的编码格式，传输首部
		outfile.flush();
		outfile.close();

		if (isExist) {
			str2 = str2 + "HTTP/1.1 204 No Content" + "\n";
		} else {
			str2 = str2 + "HTTP/1.1 201 Created" + "\n";
		}
		str2 = str2 + "Server: MyHttpServer/1.0" + "\n" + str1[2] + "\n" + str1[3] + "\n\r\n";
		try {
			ostream.write(str2.getBytes(), 0, str2.length());//传输文件信息
			ostream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("transfered file");
	}

	/**
	 * get命令的实现
	 * 
	 * @throws IOException
	 */
	private void get() throws IOException {
		String fileName = getFileName(request);//获取文件名
		File file = null;
		if (fileName.equals("/")) {
			file = new File(root + "/index.html");
		} else {
			file = new File(root + fileName);//创建出文件
		}
		if (file.isFile()) {

			long fileSize = file.length();// 得到文件大小
			DataInputStream bis = null;
			try {
				bis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			respond200(fileName);//响应200
			byte[] buffer = new byte[4 * 1024];// 设置缓存的大小，实现后面的文件的传输
			try {
				int len;
				while ((len = bis.read(buffer)) > 0) {
					ostream.write(buffer, 0, buffer.length);//写入文件信息

					ostream.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					ostream.close();
					socket.close();
					//关闭流及套接字
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			respond404();// not found响应
		}

	}

	/**
	 * @param request:
	 *            request is the comment client sent to server
	 * @return fileName: name of file in commend 获取命令中的文件名，并返回
	 */
	public String getFileName(String request) {
		String[] split = request.split(" ", 3);
		String fileName = split[1];
		return fileName;
	}

	/**
	 * 200状态码的响应
	 * 
	 * @param fPath
	 */
	private void respond200(String path) {
		/**
		 * 要点：注意报文的格式(规范化）
		 */
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 取得当前时间
		String time = format.format(date);
		//以下 为设置报文的内容及格式
		String status = "HTTP/1.0 200 OK" + CRLF;
		status = status + "Connection: Close" + CRLF;
		status = status + "Date:" + time + CRLF + "Content-tpye: ";
		StringTokenizer stringTokenizer = new StringTokenizer(path, ".");
		stringTokenizer.nextToken();
		String form = stringTokenizer.nextToken(); // 解析出第一部分
		if (form.equals("html")) {
			status = status + "text/html" + "\n";
		} else if (form.equals("jpg")) {
			status = status + "jpg" + "\n";
		} else {
			status = status + "unknown" + "\n";
		}
		status = status + "Content-length: " + new File(root + path).length() + "\r\n" + "\r\n";
		try {
			ostream.write(status.getBytes(), 0, status.length());//文件信息写入
			ostream.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
		System.out.println("response send");
	}

	/**
	 * 404状态码的响应
	 */
	private void respond404() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 取得当前时间
		String time = format.format(date);
		String response = "HTTP/1.1 404 Not Found" + CRLF + "Date: " + time + CRLF + "Server: 127.0.0.1" + CRLF
				+ "Connection: close" + CRLF + "Content-Length: ";
		int t = response.length();
		response += t + CRLF + CRLF;
		try {
			ostream.write(response.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ostream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 400状态码的响应
	 */
	private void respond400() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 取得当前时间
		String time = format.format(date);
		String response = "HTTP/1.1 400 Bad Request" + CRLF + "Date: " + time + CRLF + "Server: 127.0.0.1" + CRLF
				+ "Connection: close" + CRLF + "Content-Length: ";
		int t = response.length();
		response += t + CRLF + CRLF;
		try {
			ostream.write(response.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ostream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param length
	 * @return
	 * @throws IOException
	 */
	private String getData(long length) throws IOException {

		StringBuffer data = new StringBuffer();
		long fileLength = length;
		long n = fileLength / buffer_size;
		long finalLength = fileLength - buffer_size * n;
		byte[] buffer = new byte[buffer_size];

		while (n > 0) {
			istream.read(buffer);
			data.append(new String(buffer, "iso-8859-1"));
			n--;
		}
		byte[] finalBuffer = new byte[(int) finalLength];
		istream.read(finalBuffer);
		data.append(new String(finalBuffer, "iso-8859-1"));
		return data.toString();
	}
}
