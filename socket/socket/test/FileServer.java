package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import past.ServerThread;

/**
 * tcp echo test, server side
 * 
 * @author wben
 *
 */
public class FileServer {
	ServerSocket serverSocket;
	private final int PORT = 2021; //�˿�

	public FileServer() throws IOException {
		serverSocket = new ServerSocket(PORT, 2); // �������������׽���
		serverSocket.setSoTimeout(30000);//���õȴ�ʱ��30s
		System.out.println("������������");
	}

	public static void main(String[] args) throws IOException {
		new FileServer().servic(); // ��������
	}
	
	/**
	 * service implements
	 */
	public void servic() {
		Socket socket = null;
		while (true) {
			try {
				socket = serverSocket.accept(); //�ȴ���ȡ���û����ӣ��������׽���
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				//���������ͻ���д��Ϣ
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						socket.getOutputStream()));
				int i=0;
				PrintWriter pw = new PrintWriter(bw, true); //װ���������true,ÿдһ�о�ˢ�����������������flush
				String info = null; //�����û��������Ϣ
				while ((info = br.readLine()) != null) {
					i++;
					//System.out.println(info); //����û����͵���Ϣ
					new Thread(new ServerThread(socket,i)).start();//�̵߳Ĵ���
					if (info.equals("bye")) { //����û����롰quit�����˳�
						pw.println("ByeBye" );
						break;
					}
				}
			} //����ͻ��˶Ͽ����ӣ���Ӧ������쳣������Ӧ�ж�����whileѭ����ʹ�÷������ܼ����������ͻ���ͨ�� 
			catch (IOException e) {
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
	}
	
	

}
