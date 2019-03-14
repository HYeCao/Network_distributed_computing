package src.ssd8.socket.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

class Deal implements DealRequest{

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;
	/**
	 * The end of line character sequence.
	 */
	private static String CRLF = "\r\n";
	
	private static String Root="D:/images";

	public Deal() {
	}
	@Override
	public void Handle(String method, String body, StringBuffer header, BufferedOutputStream ostream) throws IOException {
		switch (method) {
		case "GET":
			doGet(header,body,ostream);
			break;
		case "PUT":
			doPut(header,body,ostream);
			break;
		default:
			doOther(header,body,ostream);
			break;
		}
	}

	@SuppressWarnings("resource")
	public void doGet(StringBuffer header,String body,BufferedOutputStream ostream) throws IOException{
		String hString = header.toString();
		String httpVersion = "HTTP/1.0";
		String response = "";
		StringTokenizer sTokenizer=new StringTokenizer(hString, "\n");
		if(sTokenizer.hasMoreTokens())
		{
			StringTokenizer stringTokenizer = new StringTokenizer(sTokenizer.nextToken(), " ");
			if(stringTokenizer.countTokens()>=2)
			{
				stringTokenizer.nextToken();
				String fName = stringTokenizer.nextToken();
				httpVersion = stringTokenizer.nextToken();
				File file = new File(Root+fName.substring(1,fName.length()));
				if(file.exists()&&file.isFile())
				{
					response = httpVersion+" "+"200"+" "+"Ok";
					response += CRLF;
					response += CRLF;
					ostream.write(response.getBytes());
					FileInputStream in = new FileInputStream(file);
					byte[] buffer = new byte[buffer_size];
					while(in.read(buffer) != -1)
					{
						ostream.write(buffer);
					}
					ostream.flush();
					in.close();
				}
				else {
					response = httpVersion+" "+"404"+" "+"Not Found"+CRLF;
					response += "Connection: close"+CRLF;
					response += "Content-Length: 0"+CRLF;
					response += CRLF;
					ostream.write(response.getBytes());
					ostream.flush();
				}
			}
			else {
				response=httpVersion+" "+"400"+" "+"Bad Request";
				response+=CRLF;
				response+=CRLF;
				ostream.write(response.getBytes());
				ostream.flush();
			}
		}
		else {
			response=httpVersion+" "+"400"+" "+"Bad Request";
			response+=CRLF;
			response+=CRLF;
			ostream.write(response.getBytes());
			ostream.flush();
		}
		System.out.println("Response: "+response);
	}

	@SuppressWarnings("resource")
	public void doPut(StringBuffer header,String body,BufferedOutputStream ostream) throws IOException {
		String hString=header.toString();
		String httpVersion="HTTP/1.0";
		String response="";
		StringTokenizer sTokenizer=new StringTokenizer(hString, "\n");
		if(sTokenizer.hasMoreTokens())
		{
			StringTokenizer stringTokenizer=new StringTokenizer(sTokenizer.nextToken(), " ");
			if(stringTokenizer.countTokens()>=2)
			{
				stringTokenizer.nextToken();
				String fName=stringTokenizer.nextToken();
				httpVersion=stringTokenizer.nextToken();
				FileOutputStream outfile = new FileOutputStream(Root+fName);
				outfile.write(body.getBytes());
				outfile.flush();
				outfile.close();
				response=httpVersion+" "+"200"+" "+"Ok";
				response+=CRLF;
				response+=CRLF;
				ostream.write(response.getBytes());
				ostream.flush();
			}
			else {
				response=httpVersion+" "+"400"+" "+"Bad Request";
				response+=CRLF;
				response+=CRLF;
				ostream.write(response.getBytes());
				ostream.flush();
			}
		}
		else {
			response=httpVersion+" "+"400"+" "+"Bad Request";
			response+=CRLF;
			response+=CRLF;
			ostream.write(response.getBytes());
			ostream.flush();
		}
	}

	public void doOther(StringBuffer header,String body,BufferedOutputStream ostream) throws IOException{
		String httpVersion="HTTP/1.0";
		String response="";
		response=httpVersion+" "+"400"+" "+"Bad Request";
		response+=CRLF;
		response+=CRLF;
		ostream.write(response.getBytes());
		ostream.flush();
	}
}