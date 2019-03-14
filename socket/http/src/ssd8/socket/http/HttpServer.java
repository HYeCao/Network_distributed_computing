package src.ssd8.socket.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * Default port is 80.
	 */
	private static final int PORT = 80;

	/**
	 * String to represent the Carriage Return and Line Feed character sequence.
	 */
	static private String CRLF = "\r\n";

	public HttpServer() {
	}

	@SuppressWarnings("resource")
	public void CreateRequestDeal(DealRequest dealRequest) throws IOException {
		ServerSocket sSocket = new ServerSocket(PORT);
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*4);
		while (true) {
			Socket socket = sSocket.accept();
			ServerThread serverThread=new ServerThread(socket, dealRequest);
			executorService.execute(serverThread);
		}
	}

	class ServerThread implements Runnable{
		Socket socket;
		DealRequest dealRequest;
		String method;
		private byte[] buffer;
		String body;
		StringBuffer header;
		BufferedInputStream istream = null;
		BufferedOutputStream ostream = null;
		BufferedReader iBufferedReader=null;

		public ServerThread(Socket socket,DealRequest dealRequest) throws IOException {
			this.socket = socket;
			this.dealRequest = dealRequest;
			ostream = new BufferedOutputStream(socket.getOutputStream());
			/*iBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			*/istream=new BufferedInputStream(socket.getInputStream());
			buffer = new byte[buffer_size];
			header=new StringBuffer();
			body="";
		}

		@Override
		public void run() {
			try {
				processRequest();

				method=methodType(header.toString());
				if(method!=null)
				dealRequest.Handle(method, body, header, ostream);
				else {
					String httpVersion="HTTP/1.0";
					String response=httpVersion+" "+"400"+" "+"Bad Request";
					response+=CRLF;
					response+=CRLF;
					ostream.write(response.getBytes());
					ostream.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				
			}
		}
		
		private void processRequest() throws IOException  {
			int last = 0, c = 0;
			/**
			 * Process the header and add it to the header StringBuffer.
			 */
			boolean inHeader = true; // loop control
			while (inHeader) {
				c = istream.read();
				if(c==-1)
					break;
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
			System.out.println("header: "+header);
			/**
			 * Read the contents and add it to the response StringBuffer.
			 */
			iBufferedReader=new BufferedReader(new InputStreamReader(istream));
			StringBuffer stringBuffer=new StringBuffer();
			while (iBufferedReader.ready()) {
				System.out.println("x");
				istream.read(buffer);
				stringBuffer.append(new String(buffer,"iso-8859-1"));
			}
			body=stringBuffer.toString();
			System.out.println("body: "+body);
			
			/*int last = 0, c = 0;
			*//**
			 * Process the header and add it to the header StringBuffer.
			 *//*
			boolean inHeader = true; // loop control
			while (inHeader) {
				c = istream.read();
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
			System.out.println("Header£º "+header.toString());
			*//**
			 * Read the contents and add it to the response StringBuffer.
			 *//*
			char cbuf[]=new char[buffer_size];
			while (istream.ready()) {
				istream.read(cbuf);
				body.append(new String(buffer,"iso-8859-1"));
			}
			System.out.println("Body£º "+body.toString());*/
		}
		
		private String methodType(String header) {
			StringTokenizer stringTokenizer=new StringTokenizer(header, "\n");
			if(stringTokenizer.hasMoreTokens())
			{
				StringTokenizer stringTokenizer2=new StringTokenizer(stringTokenizer.nextToken(), " ");
				if(stringTokenizer2.hasMoreTokens())
					return stringTokenizer2.nextToken();
			}
			return null;
		}
		
	}
}
