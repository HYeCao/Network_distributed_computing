package exam1_2;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author wben
 */

/**
 * @author dell
 *
 */
public class HttpClient {

	/**
	 * default HTTP port is port 80
	 */
	private static int port = 80;

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * Response is stored in a byte array.
	 */
	private byte[] buffer;

	/**
	 * My socket to the world.
	 */
	Socket socket = null;

	/**
	 * Default port is 80.
	 */
	private static final int PORT = 8888;

	/**
	 * Output stream to the socket.
	 */
	BufferedOutputStream ostream = null;

	/**
	 * Input stream from the socket.
	 */
	BufferedInputStream istream = null;

	/**
	 * StringBuffer storing the header
	 */
	private StringBuffer header = null;

	/**
	 * StringBuffer storing the response.
	 */
	private StringBuffer response = null;

	/**
	 * String to represent the Carriage Return and Line Feed character sequence.
	 */
	static private String CRLF = "\r\n";
	private static String root = "D:\\";

	/**
	 * HttpClient constructor;
	 */
	public HttpClient() {
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		response = new StringBuffer();
	}

	/**
	 * <em>connect</em> connects to the input host on the default http port --
	 * port 80. This function opens the socket and creates the input and output
	 * streams used for communication.
	 */
	public void connect(String host) throws Exception {

		/**
		 * Open my socket to the specified host at the default port.
		 */
		socket = new Socket(host, PORT);

		/**
		 * Create the output stream.
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());

		/**
		 * Create the input stream.
		 */
		istream = new BufferedInputStream(socket.getInputStream());
	}

	/**
	 * <em>processGetRequest</em> process the input GET request.
	 */
	public void processGetRequest(String request) throws Exception {
		/**
		 * Send the request to the server.
		 */
		request += CRLF + "Host: www.baidu.com" + CRLF + CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * waiting for the response.
		 */
		processResponse();
	}

	/**
	 * <em>processPutRequest</em> process the input PUT request.
	 * put命令的实现
	 */
	public void processPutRequest(String request) throws Exception {

		// =======start your job here============//

		String[] str = request.split(" ");
		/**
		 * 解析出文件名，并创建文件（等待数据传入）(注意处理目录的情况）
		 */
		File file = new File("D:" + str[1]);
		
		/**
		 * 以下为判别指定的文件名是否存在于指定目录下
		 */
		if(!file.exists()){
			this.header.append("HTTP/1.0 400 bad request" + "\n");// 400 错误命令
			return;
		}
		/**
		 * 以下为判别命令是否符合输入的要求
		 */
		if (str.length != 3) {
			this.header.append("HTTP/1.0 400 bad request" + "\n");// 400 错误命令
			return;
		}
		/**
		 * 以下为设置报文的首部格式
		 */
		request = request + "\n"+"Host: MyHttpClient\n";
		/**
		 * 对于文件类型的判别，通过stringTokenizer进行分割获取
		 */
		StringTokenizer stringTokenizer = new StringTokenizer(str[1], ".");
		stringTokenizer.nextToken();
		String form = stringTokenizer.nextToken(); // 解析出第一部分
		if(form.equals("jpg")){
			request = request + "Content-tpye: jpg" + "\n";
		}
		else if (form.equals("html")) {
			request = request + "Content-tpye: text/html" + "\n";
		} else {
			request = request + "Content-tpye: unknown" + "\n";
		}
		request = request + "Content-length: " + new File(root + str[1]).length() + "\n" + "\r\n";// 获取传输文件的大小
		
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());// 通过套接字将信息 ，传输至服务器端（不包含具体的文件数据）						
		ostream.flush();// 刷新

		/*
		 * 以下实现了文件信息封装至套接字中，从而实现传输
		 */
		long fileLength = file.length();//获取文件大小

		long n = fileLength / buffer_size;//设置文件得读取次数
		long end = fileLength - buffer_size * n;//最后一次读取文件
		BufferedInputStream bStream = null;
		try {
			bStream = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e2) {
			
			e2.printStackTrace();
		}
		byte[] info = new byte[buffer_size];
		
		while (n > 0) {//while循环实现文件传输
			try {
				bStream.read(info, 0, buffer_size);
				ostream.write(info, 0, buffer_size);
				ostream.flush();
				TimeUnit.MICROSECONDS.sleep(1);//停顿设置，实现有序传输
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			n--;
		}
		info = new byte[(int) end];
		try {
			bStream.read(info, 0, (int) end);
			ostream.write(info, 0, (int) end);
			ostream.flush();
			bStream.close();
			//最后一次读取文件信息并传输，传输完毕，关闭流资源
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 * 以下为对首部信息进行处理,规范其格式
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
	 * <em>processResponse</em> process the server response.
	 * 处理服务器端的响应
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
		

		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		while (istream.read(buffer) != -1) {
			response.append(new String(buffer, "iso-8859-1"));
		}
		
	}

	/**
	 * Get the response header.
	 */
	public String getHeader() {
		return header.toString();
	}

	/**
	 * Get the server's response.
	 */
	public String getResponse() {
		return response.toString();
	}

	/**
	 * Close all open connections -- sockets and streams.
	 */
	public void close() throws Exception {
		socket.close();
		istream.close();
		ostream.close();
	}
}
