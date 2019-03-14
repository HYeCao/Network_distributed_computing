package ex1_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpHandler implements Runnable {

	private Socket socket;
	private File root;
	private File currentFile;
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	PrintWriter printWriter;
	
	public TcpHandler(File root, Socket socket) {
		this.root = root;
		this.currentFile = root;
		this.socket = socket;
	}
	
	public void initStream() throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter = new PrintWriter(bufferedWriter, true);
	}
	@Override
	public void run() {
		try {
			System.out.println("新连接，连接地址：" + socket.getInetAddress().getHostAddress() + "连接端口：" + socket.getPort());
			initStream();
			printWriter.println(socket.getInetAddress().getHostAddress() + ">连接成功");
			String info = null;
			while (null != (info=bufferedReader.readLine())) {
				if (info.equals("ls")) {
					for (File file : currentFile.listFiles()) {
						if (file.isFile()) {
							printWriter.println("<file>\t\t" + file.getName() + "\t\t" + file.length());
						} else if (file.isDirectory()) {
							printWriter.println("<dir>\t\t" + file.getName() + "\t\t" + file.length());
						}
						
					}
				} else if (info.startsWith("cd")) {
					String dir = info.substring(2).trim();
					File cdFile = new File(currentFile.getAbsoluteFile(), dir);
					if (cdFile.isDirectory()) {
						if (dir.equals("..")) {
							if (!currentFile.equals(root)) {
								currentFile = currentFile.getParentFile();
							} else {
								currentFile = cdFile;
							}
							printWriter.println(currentFile.getName() + "> ok");
						} else {
							printWriter.println("unknow  dir");
						}
					}
				} else if (info.startsWith("get")) {
					String files = info.substring(3).trim();
					File getFile = new File(currentFile.getAbsoluteFile(), files);
					if (getFile.isFile()) {
						printWriter.println("OK");
						printWriter.println(getFile.getAbsolutePath());
						printWriter.println(getFile.length());
					} else {
						printWriter.println("unknow file");
					}
				} else if (info.equals("bye")) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
					bufferedReader.close();
					bufferedWriter.close();
					printWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
