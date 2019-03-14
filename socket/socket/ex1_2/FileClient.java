package ex1_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class FileClient {
	private static final int TCP_PORT = 2021;
	private static final int UDP_PORT = 2020;
	private static final String HOST = "127.0.0.1";
	private static final int MAX = 1024;
	Socket socket = new Socket();
	DatagramSocket datagramSocket;

	public FileClient() throws Exception {
		socket = new Socket(HOST, TCP_PORT);
	}

	public void connect() throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter printWriter = new PrintWriter(bufferedWriter, true);
		System.out.println(bufferedReader.readLine());
		
		Scanner in = new Scanner(System.in);
		String cmd = null;
		while((cmd=in.nextLine().trim())!= null) {
			if (cmd.equals("ls") || cmd.equals("cd") || cmd.equals("bye")) {
				printWriter.println(cmd.trim());
				String msg = null;
				while ((msg = bufferedReader.readLine()) != null && !msg.equals("")) {
					System.out.println(msg);
				}
				if (cmd.equals("bye")) {
					break;
				}
			} else if (cmd.equals("get")) {
				printWriter.println(cmd.trim());
				String msg = bufferedReader.readLine();
				if (msg.equalsIgnoreCase("OK")) {
					String path = bufferedReader.readLine();
					int fileLen = Integer.parseInt(bufferedReader.readLine());
					datagramSocket = new DatagramSocket();
					byte[] info = path.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(info, info.length, new InetSocketAddress(HOST, UDP_PORT));
					datagramSocket.send(sendPacket);
					String fileName = path.substring(path.lastIndexOf(File.separatorChar));
					File receiveFile = new File(fileName);
					receiveFile.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(receiveFile);
					int times;
					if (fileLen % MAX > 0) {
						times = fileLen / MAX + 1;
					} else {
						times = fileLen / MAX;
					}
					byte[] inBuff = new byte[MAX];
					DatagramPacket receivePacket = new DatagramPacket(inBuff, MAX);
					System.out.println("开始接收文件");
					do {
						datagramSocket.receive(receivePacket);
						fileOutputStream.write(inBuff, 0, receivePacket.getLength());
						fileOutputStream.flush();
						System.out.println("file receiving ......");
					} while (receivePacket.getLength() > 0);
					fileOutputStream.close();
					datagramSocket.close();
					System.out.println("接收完毕");
					bufferedReader.readLine();
				} else {
					System.out.println(msg);
					bufferedReader.readLine();
				}
//				else {
//					
//				}
			}
		}
	}

}
