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
	 * ���մ����Socket������Ự
	 */
	private Socket socket;
	/**
	 * ������������û���ȡ�׽�������Ϣ
	 */
	BufferedOutputStream ostream = null;
	/**
	 * ����������������д���׽�����Ϣ
	 */
	BufferedInputStream istream = null;
	/**
	 * ���建�����
	 */
	private byte[] buffer;
	/**
	 * �趨ָ���Ļ����С
	 */
	private static int buffer_size = 8192;
	/**
	 * ͷ���Ķ���
	 */
	private StringBuffer header = null;
	/**
	 * �Զ���ĸ�ʽ
	 */
	private String CRLF = "\r\n";
	/**
	 * ���ڽ�����������
	 */
	private String request;

	
	private ProxyGet proxyClient;
	private URL url;

	public MyHandler(Socket socket) throws IOException {

		this.socket = socket;
		/**
		 * ��ʼ�����Լ���ַ
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());
		istream = new BufferedInputStream(socket.getInputStream());
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		this.proxyClient = new ProxyGet();
	}

	/*
	 * 
	 * get:ע��run�ṹ�Լ�get�еĴ����ж������������Ϣ�Ĵ���  (non-Javadoc)
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
				get();// ִ��get����
			} catch (IOException e) {

				e.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		} else {
			respond400();// ��Ӧ400
		}
	}

	/**
	 * �����ײ������ֽ��нṹ����֯���ײ��ĸ�ʽ����
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
	 * get�����ʵ��
	 * 
	 * @throws Exception
	 */
	private void get() throws Exception {
		URL url = getURL(request);// ��ȡURL
		if (url == null) { // �б�URL�ĸ�ʽ�Ƿ���ȷ
			respond400();
		} else {
		/**
		 * ����Ϊִ��get����
		 */
			requestGet(url);
			System.out.println("CONNECTION:" + url.getHost());
			responseGet();
		}
	}

	/**
	 * @param request:
	 *            request is the comment client sent to server
	 * @return fileName: name of file in commend ��ȡ�����е��ļ�����������
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
	 * �ж�һ��URL�Ƿ�Ϸ�
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
	 * ����url����GET����
	 *
	 * @param url
	 *            url
	 * @throws Exception
	 */
	private void requestGet(URL url) throws Exception {
		// Ĭ��ȥ����80�˿�
		/**
		 * �����������Ϊ�н� ʵ�ֵ��ǿͻ��˵�������ͻ���Ҫ�������͸��������ˣ�Ȼ���ȡ�������˵���Ӧ��Ȼ���ٷ��͸��ͻ���
		 * �á��н顱�����ͻ���Ҳ����������
		 */
		int port;//��ʼ����˿ں�

        if(url.getPort()!=-1)port=url.getPort();
        else {
        	port=80;
        }
//        System.out.println("test4");
		 proxyClient.connect(url.getHost(), port);//��ָ���ķ������˽�������
		String request = "GET " + url.getFile() + " HTTP/1.0";
		proxyClient.processGetRequest(request, url.getHost());// ��get�����װ�ú��͸���������

	}

	/**
	 * �õ�GET�ظ������ظ�����������ͻ���
	 *
	 * @throws IOException
	 */
	private void responseGet() throws IOException {

		String header = proxyClient.getHeader() + "\n";
		String body = proxyClient.getResponse();
//		System.out.println(header);//header �Ѿ�ʵ���˻�ȡ
		 /**
		  * �˴����䣬header��body���Ѿ���ȡ��ע�⴫��ʱ����ĸ�ʽ�淶��
		  */
		buffer = header.getBytes("iso-8859-1");// �趨���ض��ı����ʽ�������ײ�
		ostream.write(buffer, 0, header.length());
		ostream.write(body.getBytes("iso-8859-1"));

		ostream.flush();

		ostream.close();//���Ǵ�����ɺ����socket�Լ�����Դ�Ĺر�
		socket.close();
	}

	/**
	 * 400״̬�����Ӧ
	 */
	private void respond400() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// ȡ�õ�ǰʱ��
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
