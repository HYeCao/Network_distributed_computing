package src.ssd8.socket.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author wben
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

	static private String Root="E:/IDEAcode/ex2";
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
	 */
	@SuppressWarnings("resource")
	public void processPutRequest(String request) throws Exception {
		//=======start your job here============//
		if(request!=null&&request.startsWith("PUT"))
		{
			String fName;
			StringTokenizer stringTokenizer = new StringTokenizer(request," ");
			if(stringTokenizer.countTokens() <= 2){
				System.err.println("input error");
				return;
			}
			stringTokenizer.nextToken();
			fName = stringTokenizer.nextToken();
			System.out.println(fName);
		    File file=new File(Root+fName);
		    if(file.exists()&&file.isFile())
		    {
		    	String reqMessage=request+CRLF+CRLF;
		    	buffer=reqMessage.getBytes();
		    	ostream.write(buffer);
				FileInputStream in = new FileInputStream(file);
				while(in.read(buffer) != -1)
				{
					ostream.write(buffer);
				}
				ostream.flush();
				in.close();
		    }
		    else {
				System.err.println("File Not Exist");
			}
		}
		System.out.println("llllll");
		processResponse();
		//=======end of your job============//
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
		System.out.println("Header:"+header.toString());

		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		while (istream.read(buffer) != -1) {
			response.append(new String(buffer,"iso-8859-1"));
		}
		System.out.println("body:"+response);
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
