package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * tcp echo test, client side
 * 
 * @author wben
 *
 */
public class FileClient {
	static final int PORT = 2021; //���Ӷ˿�
	static final String HOST = "127.0.0.1"; //���ӵ�ַ
	Socket socket = new Socket();
 
	public FileClient() throws UnknownHostException, IOException {
		//socket = new Socket(HOST, PORT); //�����ͻ����׽���
		socket = new Socket();
		socket.connect(new InetSocketAddress(HOST, PORT));
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		new FileClient().send();
	}

	/**
	 * send implements
	 */
	public void send() {
		try {
			//�ͻ���������������������Ϣ
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			//�ͻ��������������շ�������Ϣ
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter pw = new PrintWriter(bw, true); //װ�����������ʱˢ��
			Scanner in = new Scanner(System.in); //�����û���Ϣ
			String msg = null;
			
			System.out.println( socket.getInetAddress()+":"+socket.getPort()+"> ���ӳɹ�");
			while ((msg = in.nextLine()) != null) {
				pw.println(msg); //���͸���������
				
				if(br.readLine().equals(" "))
				System.out.println(br.readLine()); //������������ص���Ϣ
				if (msg.equals("quit")) {
					break; //�˳�
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close(); //�Ͽ�����
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	 public static void print(File f){
//	        if(f!=null){
//	            if(f.isDirectory()){
//	                File[] fileArray=f.listFiles();
//	                if(fileArray!=null){
//	                    for (int i = 0; i < fileArray.length; i++) {
//	                        //�ݹ����
//	                        print(fileArray[i]);
//	                    }
//	                }
//	            }
//	            else{
////	            	pw.println("your address:" + f);
//	                System.out.println(f);
//	            }
//	        }
//	 }
}
