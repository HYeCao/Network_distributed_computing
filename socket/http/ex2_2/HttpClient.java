package ex2_2;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;


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
	private static final int PORT = 80;

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
		request += CRLF + CRLF;
		//System.out.println(request);
		buffer = request.getBytes();
		//System.out.write(buffer);
		//����Ϣ���������
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * waiting for the response.
		 */
		//System.out.println("request done");
		processResponse();
	}
	
	/**
	 * <em>processPutRequest</em> process the input PUT request.
	 */
	public void processPutRequest(String request) throws Exception {
		//=======start your job here============//
		String[] strings = request.split(" ");
		if(strings.length==2||strings.length==3){
			String[] filePath = strings[1].split("/");//��ȡ����ĸ���������
		}else{
			this.header.append("HTTP/1.0 400 bad request" + "\n");//400 ��������
			return;
		}
		
		if(isFile(strings[1])==false||strings[1].split("\\.").length<1){
			this.header.append("HTTP/1.0 400 bad request" + "\n");
			return;
		}
		//System.out.println(strings[1].split("\\.").length);
		/**
		 * ����Ϊ���ñ��ĵ��ײ���ʽ
		 */
		request = request + "\nHost: MyHttpClient\n" ;
		String houzhui = strings[1].split("\\.")[strings[1].split("\\.").length-1];
		if(houzhui.equals("jpg")){
			request = request + "Content-tpye: application/x-jpg" + "\n";
		}else if(houzhui.equals("html")){
			request = request + "Content-tpye: test/html" + "\n";
		}else{
			request = request + "application/octet-stream" + "\n";
		}
		//System.out.println(getLength(new File(root+strings[1])));
		request = request + "Content-length: " + getLength(new File(root+strings[1])) + "\n" +"\r\n";//��ȡ�����ļ��Ĵ�С
		//System.out.println(request);
		//System.out.println("send done");
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());//ͨ���׽��ֽ���Ϣ �������������ˣ�������������ļ����ݣ�
		ostream.flush();//ˢ��
		 
		transformData(strings[1]);//���������ļ�����; strings[1]Ϊ�ļ�������
		
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
		//System.out.println("start read response");
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
		//=======end of your job============//
	}
	private void transformData(String fPath) {
		
		String[] ff = fPath.split("/");
		
		String path = "";
		path = path + root;
		for(int i=0;i<ff.length-1;i++){
			path = path + ff[i] + "\\";
		}
		path = path + ff[ff.length-1];
		String filename = path;
		
		File file = new File(filename);//��ʱ��ȷ�����ļ��ľ���·��
		long fileLength = getLength(file);//��ȡ���ļ��Ĵ�С
		
		long n = fileLength / buffer_size;//�趨����ʱ�ֽڵĻ����С
		long finalLength = fileLength - buffer_size*n;//ȷ�����һ�δ���ʱ���ļ���С
	
		BufferedInputStream bStream = null;
		try {
			bStream = new BufferedInputStream(new FileInputStream(file));//�趨�ļ���������
		} catch (FileNotFoundException e2) {
			
			e2.printStackTrace();
		}
		byte[] info = new byte[buffer_size];
		
		while (n > 0){
			try {
				bStream.read(info, 0, buffer_size);//��ȡ�ļ��е���Ϣ
				ostream.write(info, 0, buffer_size);//���ļ�����Ϣ�������׽��ֵ��������
				ostream.flush();
				//�������д���ʵ�����ļ���Ϣ���׽��ִ���
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			n--;
		}
		info = new byte[(int) finalLength];//�����ļ����β���Ĵ�С����ʵ�ִ���
		try {
			bStream.read(info, 0, (int) finalLength);
			ostream.write(info, 0, (int) finalLength);
			ostream.flush();
			bStream.close();
			//��ʱ��ʵ�ֵ������ļ��Ĵ��䣬���ر����յ��׽����Լ��ļ�������
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
	}
	/**
	 * ��ȡ�ļ��Ĵ�С��Long����)
	 * @param f
	 * @return
	 */
	private long getLength(File f) {
		// TODO Auto-generated method stub
		long length = 0;
		if(f.isDirectory()){
			File[] list = f.listFiles();
			for(File ff:list){
				length = length + getLength(ff);
			}
		}else{
			return f.length();
			//System.out.println(String.valueOf(length));
		}
		return length;
	}
	/**
	 * �б��Ƿ�Ϊ�ļ�����
	 * @param f
	 * @return
	 */
	private boolean isFile(String f){
		String[] ff = f.split("/");
		//System.out.println(ff.length);
		String path = "";
		path = path + root;
		for(int i=0;i<ff.length-1;i++){
			path = path + ff[i] + "\\";
			//System.out.println(path);
			File file = new File(root + ff[0]);
			if(file.isDirectory()==false){
				//r404();
				//transformData("/400.html");
				return false;
			}
		}
		path = path + ff[ff.length-1];
		//System.out.println(path);
		File file = new File(path);
		//System.out.println(path);
		if(file.exists()&&file.isFile()){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * <em>processResponse</em> process the server response.
	 * 
	 */
	public void processResponse() throws Exception {
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
		//System.out.println("start read");
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
		//System.out.println(header);
		//System.out.println("headline doneeeeeeeeeee");
		String[] string = header.toString().split("\n");
		//String length = string[3].split(" ")[1];
		String length = null;
		for(int i=0;i<string.length;i++){
			if(string[i].startsWith("Content-length")){
				 length = string[i].split(" ")[1];
			}
		}
		if(length==null){
			response = response.append(new String("error".getBytes(),"iso-8859-1"));
			return;
		}
		//System.out.println(length);
		
		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		long fileLength = Long.valueOf(length);
		long n = fileLength / buffer_size;
		long finalLength = fileLength - buffer_size*n;
		byte[] buffer = new byte[buffer_size];
		//System.out.println(n);
		while (n>0) {//
			istream.read(buffer);
			response.append(new String(buffer,"iso-8859-1"));
			//System.out.println(response.toString());
			n--;
		}
		byte[] finalBuffer = new byte[(int) finalLength];
		istream.read(finalBuffer);
		response.append(new String(finalBuffer,"iso-8859-1"));
		
		//System.out.println(new String(buffer,"iso-8859-1"));
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
