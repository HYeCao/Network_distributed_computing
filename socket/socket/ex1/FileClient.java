package ex1;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import src.ssd8.socket.udp.echo.EchoClient;

/**
 * tcp echo test, client side udp transfer ��client side
 * 
 * @author Cao Hongye
 *
 */
public class FileClient {
	/**
	 * ȫ�ֱ����Ķ���
	 */
	static final int PORT = 2021; // ���Ӷ˿�
	static final String HOST = "127.0.0.1"; // ���ӵ�ַ
	static Socket socket = new Socket();
	int remotePort = 2020; // UDP�������˿�
	static String remoteIp = "127.0.0.1"; // ������IP
	static DatagramSocket Usocket; // �ͻ���DatagramSocket
	static PrintWriter pw;
	static BufferedReader br;
/**
 * ���ܣ� ��TCP�������Լ��������������Ϊһ��main����֮�У�ʵ�ֻ�������Ĺ���
 * 
 * UDP���������⣬��Ҫת���������˽��д�������FileServer)
 * @param args
 * @throws UnknownHostException
 * @throws IOException
 */
	public static void main(String[] args) throws UnknownHostException, IOException {

		socket = new Socket();
		socket.connect(new InetSocketAddress(HOST, PORT));
		String path = "D:/";// Ĭ�ϱ���Ŀ¼
//		int port = 2020;// UDP�˿ں�
//		Usocket = new DatagramSocket(port); // �����DatagramSocket����

		try {
			// �ͻ���������������������Ϣ
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			// �ͻ��������������շ�������Ϣ
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(bw, true); // װ�����������ʱˢ��
			Scanner in = new Scanner(System.in); // �����û���Ϣ
			String msg = null;

			System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "> ���ӳɹ�");
			while ((msg = in.nextLine()) != null) {
				StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
				String first = stringTokenizer.nextToken(); // ��������һ����
				if (first.equals("bye")) {
					
					pw.println("bye");// ������־
					Usocket.close();
					socket.close();
					break;
				}
				pw.println(msg); // ���͸���������
				String msg1 = br.readLine();// ���ж�ȡ�������˵�����

				while (!msg1.equals("\0")) {// ��ȡ������־

					System.out.println(msg1);

					if (msg1.equals("begin transfor")) {// get���������
						UDPget(msg);
					}
					msg1 = br.readLine();

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close(); // �Ͽ�����
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/***
	 * ����:UDP�Ľ����ļ�ʵ��
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	private static void UDPget(String fileName) throws IOException {

		String path = "D:/";// Ĭ�ϱ���Ŀ¼
		int port = 2020;// UDP�˿ں�
		StringTokenizer stringTokenizer = new StringTokenizer(fileName, " ");
		String first = stringTokenizer.nextToken(); // ��������һ����
		String fn = stringTokenizer.nextToken();// �������ļ���
		byte[] receiveData = new byte[8192];// ��������
		int buffLen = receiveData.length;// �����С

		DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path + fn)));
		/*
		 * �·�Ϊѭ�������ļ���ʵ�� ������ ͨ�����õ�һ����iʵ��ѭ���Ľ��գ�ͬʱ�����ļ�������������ȡ����д���ļ�֮��
		 * ע��㣺���ǵùر��ļ�������socket
		 */
		int i = 0;		
			Usocket = new DatagramSocket(); 	
			SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 2020);
			DatagramPacket dp=new DatagramPacket("".getBytes(),"".getBytes().length,socketAddress);
			Usocket.send(dp);
			
		while (i == 0) {
			// ���տͻ�����Ϣ
			byte[] receiveByte = new byte[1024];
			
		
			DatagramPacket dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
			Usocket.receive(dataPacket);
			i = dataPacket.getLength();
			// System.out.println(i);//�жϽ��յĳ���
			// ��������
			if (i > 0) {
				// �������ݳ���ѭ����������
				fileOut.write(receiveByte, 0, i);
				fileOut.flush();
				i = 0;// ѭ������������ʵ��
			}
			if (new String(dataPacket.getData(), 0, dataPacket.getLength()).equals("\n")) {
				System.out.println("File received");
				fileOut.close();
				Usocket.close();
				break;
			}

		}
	}

}
