package exam1;

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
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import exam1.ProxyGet;

//import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

public class MyHandler implements Runnable {
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

	
	private ProxyGet proxyClient;
	private URL url;

	public MyHandler(Socket socket) throws IOException {

		this.socket = socket;
		/**
		 * 初始化流以及地址
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());
		istream = new BufferedInputStream(socket.getInputStream());
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		this.proxyClient = new ProxyGet();
	}

	/*
	 * 
	 * get:注意run结构以及get中的代码中对于输入错误信息的处理  (non-Javadoc)
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
//		System.out.println(request);
		System.out.println("transfered Header");

		if (request.startsWith("GET")) {
			try {
				get();// 执行get命令
			} catch (IOException e) {

				e.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		} else {
			respond400();// 响应400
		}
	}

	/**
	 * 对于首部的文字进行结构的组织（首部的格式处理）
	 * 
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
	 * get命令的实现
	 * 
	 * @throws Exception
	 */
	private void get() throws Exception {
		URL url = getURL(request);// 获取URL
		if (url == null) { // 判别URL的格式是否正确
			respond400();
		} else {
		/**
		 * 以下为执行get操作
		 */
			requestGet(url);
			System.out.println("CONNECTION:" + url.getHost());
			responseGet();
		}
	}

	/**
	 * @param request:
	 *            request is the comment client sent to server
	 * @return fileName: name of file in commend 获取命令中的文件名，并返回
	 */
	public URL getURL(String request) {
		//GET http://henry.somesite.com:1030/index.html HTTP/1.0
		String[] split = request.split(" ", 3);
		try {
			url = new URL(split[1]);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean u = isUrl(split[1]);
//		System.out.println(u);
		if (u == true) {
			return url;
		} else {
			return null;
		}
	}

	/**
	 * 判断一个URL是否合法
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String url) {
		String pattern = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$";
		Pattern httpPattern = Pattern.compile(pattern);
		if (httpPattern.matcher(url).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据url发送GET请求
	 *
	 * @param url
	 *            url
	 * @throws Exception
	 */
	private void requestGet(URL url) throws Exception {
		// 默认去连接80端口
		/**
		 * 代理服务器作为中介 实现的是客户端的命令，将客户端要求的命令发送给服务器端，然后获取服务器端的响应，然后再发送给客户端
		 * 该“中介”既做客户端也做服务器端
		 */
		int port;//初始定义端口号

        if(url.getPort()!=-1)port=url.getPort();
        else {
        	port=80;
        }
//        System.out.println("test4");
		 proxyClient.connect(url.getHost(), port);//与指定的服务器端建立链接
		String request = "GET " + url.getFile() + " HTTP/1.0";
		proxyClient.processGetRequest(request, url.getHost());// 将get命令封装好后发送给服务器端

	}

	/**
	 * 得到GET回复，返回给代理服务器客户端
	 *
	 * @throws IOException
	 */
	private void responseGet() throws IOException {

		String header = proxyClient.getHeader() + "\n";
		String body = proxyClient.getResponse();
//		System.out.println(header);//header 已经实现了获取
		 /**
		  * 此处传输，header和body均已经获取，注意传输时代码的格式规范化
		  */
		buffer = header.getBytes("iso-8859-1");// 设定的特定的编码格式，传输首部
		ostream.write(buffer, 0, header.length());
		ostream.write(body.getBytes("iso-8859-1"));

		ostream.flush();

		ostream.close();//谨记传输完成后对于socket以及流资源的关闭
		socket.close();
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

}
