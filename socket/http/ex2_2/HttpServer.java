package ex2_2;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import javax.naming.directory.InvalidSearchFilterException;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.xml.crypto.Data;

import org.omg.CORBA.Request;

public class HttpServer implements Runnable {

	private Socket socket;
	BufferedOutputStream ostream = null;
	BufferedInputStream istream = null;
	
	private byte[] buffer;
	private static int buffer_size = 8192;
	private StringBuffer header = null;
	
	private String headerS;
	private static String PATH = "";
	private String status;//
	private static String root = "D:\\images";
	
	public HttpServer(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}
	
	public void initStream() throws IOException{  //初始化输入输出流对象方法
		ostream = new BufferedOutputStream(socket.getOutputStream());
		istream = new BufferedInputStream(socket.getInputStream());
		buffer = new byte[buffer_size];
		header = new StringBuffer();
	}
	
	public void run(){
		try{
			initStream();
			//System.out.println("HERE1");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//读到request报文的headline, 判断是get还是put， 然后分别处理
		int last = 0, c = 0;
		boolean inHeader = true; // loop control
		try {
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
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		headerS = header.toString();
		System.out.println(headerS);
		System.out.println("header done");
		
		if(headerS.startsWith("GET")){
			get();
		}else if(headerS.startsWith("PUT")){
			try {
				put();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//System.out.println("Bad request!");
		}
	}
	
	private void put() throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		String[] hang = headerS.split("\n");
		long length = 0;
		for(int i =0;i<hang.length;i++){
			if(hang[i].startsWith("Content-length")){
				length= Long.valueOf(hang[i].split(" ")[1]);
			}
		}
		String fileName = root + "\\" + hang[0].split(" ")[1].split("/")[hang[0].split(" ")[1].split("/").length-1];
		//System.out.println(fileName);
		File file = new File(fileName);
		FileOutputStream outfile = null;
		Boolean isExist;
		if(file.exists()){
			isExist = true;
		}else{
			isExist = false;
		}
		try {
			outfile = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = null;
		try {
			data = getData(length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outfile.write(data.getBytes("iso-8859-1"));
		outfile.flush();
		outfile.close();
		
		
		String rString = "";
		if(isExist){
			rString = rString + "HTTP/1.1 204 No Content" + "\n"  + "Content-Location: ";
		}else{
			rString = rString + "HTTP/1.1 201 Created" + "\n" + "Content-Location: ";
		}
		rString = rString +hang[0].split(" ")[1].split("/")[hang[0].split(" ")[1].split("/").length-1] + "\n";
		rString = rString + "Server: MyHttpServer/1.0" + "\n" + hang[2] + hang[3] + "\n\r\n";
		try { 
			ostream.write(rString.getBytes(), 0, rString.length());
			ostream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("hang done");
	}

	private String getData(long length) throws IOException {
		// TODO Auto-generated method stub
		StringBuffer data = new StringBuffer();
		long fileLength = length;
		long n = fileLength / buffer_size;
		long finalLength = fileLength - buffer_size*n;
		byte[] buffer = new byte[buffer_size];
		//System.out.println(n);
		while (n>0) {//
			istream.read(buffer);
			data.append(new String(buffer,"iso-8859-1"));
			n--;
		}
		byte[] finalBuffer = new byte[(int) finalLength];
		istream.read(finalBuffer);
		data.append(new String(finalBuffer,"iso-8859-1"));
		return data.toString();
	}

	private static String status1 = "";
	private static String status2 = "";
	private static String status3 = "";
	
	private void get(){
		String [] command = headerS.split(" ");
		//System.out.println(command.length);
		switch (command.length) {
		case 2:
			status1 = "HTTP/1.1";
			break;
		case 3:
			//System.out.println(command[2]);
			if(command[2].equals("HTTP/1.0\n")){
				status1 = "HTTP/1.0";
				break;
			}else if(command[2].equals("HTTP/1.1\n")){
				status1 = "HTTP/1.1";
				break;
			}
			status1 = "HTTP/1.0";
			r400();
			transformData("/400.html");
			return;
		default:
			status1 = "HTTP/1.1";
			r400();
			transformData("/400.html");
			return;
		}
		
		//System.out.println("mulu");
		if(command[0].equals("GET")){
			if(isFile(command[1])){
				ok(command[1]);
				transformData(command[1]);
			}else{
				r404();
				transformData("/404.html");
			}
			
		}else{
			r400();
			transformData("/400.html");
			return;
		}
	}
	
	private void transformData(String fPath) {
		// TODO Auto-generated method stub
		String[] ff = fPath.split("/");
		//System.out.println(ff.length);
		String path = "";
		path = path + root;
		for(int i=0;i<ff.length-1;i++){
			path = path + ff[i] + "\\";
		}
		path = path + ff[ff.length-1];
		String filename = path;
		//System.out.println(filename);
		File file = new File(filename);
		long fileLength = getLength(file);
		
		long n = fileLength / buffer_size;
		long finalLength = fileLength - buffer_size*n;
		//System.out.println(fileLength);
		//System.out.println(n);
		//System.out.println(finalLength);
		//System.out.println(n);
		BufferedInputStream bStream = null;
		try {
			bStream = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		byte[] info = new byte[buffer_size];
		//int len = 0;
		while (n > 0){
			try {
				bStream.read(info, 0, buffer_size);
				ostream.write(info, 0, buffer_size);
				ostream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			n--;
		}
		info = new byte[(int) finalLength];
		try {
			bStream.read(info, 0, (int) finalLength);
			ostream.write(info, 0, (int) finalLength);
			ostream.flush();
			bStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void ok(String fPath){
		//System.out.println("OK");
		//status1 = "HTTP/1.1";
		status2 = "200";
		status3 = "OK";
		status = status1 + " " + status2 + " " + status3 +"\n";
		status = status + "Server: MyHttpServer/1.0" + "\n" 
				+ "Content-tpye: ";
		if(fPath.endsWith("\\.html")){
			status = status + "test/html" + "\n";
		}else if(fPath.endsWith("\\.jpg")){
			status = status + "application/x-jpg" + "\n";
		}else{
			status = status + "application/octet-stream" + "\n";
		}
		status = status	+ "Content-length: " +getLength(new File(root+fPath)) + "\n" +"\r\n";
		//System.out.println(getLength(new File(root+fPath)));
		//System.out.println(status);
		try {
			ostream.write(status.getBytes(), 0, status.length());
			ostream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(status);
		System.out.println("response send");
	}
	
	

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
		File file = new File(path);
		//System.out.println(path);
		if(file.exists()&&file.isFile()){
			return true;
		}else{
			return false;
		}
	}
	
	private void r404(){
		System.out.println("404");
		status2 = "404";
		status3 = "Not Found";
		status = status1 + " " + status2 + " " + status3 +"\n";
		status = status + "Server: MyHttpServer/1.0" + "\n" 
				+ "Content-tpye: test/html" + "\n" 
				+ "Content-length: " + getLength(new File(root+"/404.html")) +"\n" + "\r\n";
		try { 
			ostream.write(status.getBytes(), 0, status.length());
			ostream.flush();
			System.out.println(status.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(status);
		System.out.println("response send");
	}
	
	private void r400(){
		//System.out.println("400");
		//status1 = "HTTP/1.1";
		status2 = "400";
		status3 = "Bad Request";
		status = status1 + " " + status2 + " " + status3 +"\n";
		status = status + "Server: MyHttpServer/1.0" + "\n" 
				+ "Content-tpye: test/html" + "\n" 
				+ "Content-length: " + getLength(new File(root+"/400.html")) +"\n" + "\r\n";
		try {
			ostream.write(status.getBytes(), 0, status.length());
			ostream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(status);
		System.out.println("response send");
	}
	
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
}
