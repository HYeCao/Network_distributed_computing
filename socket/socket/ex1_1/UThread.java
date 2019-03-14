package ex1_1;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author Cao Hongye
 *
 */
public class UThread implements Runnable {

	private DatagramSocket Usocket;
	private static final int MAX = 1024;

	public UThread(DatagramSocket datagramSocket) {
		this.Usocket = datagramSocket;
	}

	@Override
	public void run() {
		while (true) {
			try {
				download();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
/*
 * ʵ���ļ��Ĵ���
 */
	private void download() throws IOException {
		while (true) {
			Scanner in = new Scanner(System.in);
			// System.out.println("Test2");//���Դ���
			DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);

			Usocket.receive(dp);
			// System.out.println("Test3");���Դ���

			// �������˵�ַ
			String msg = new String(dp.getData(), 0, dp.getLength());
			File f = new File(msg);
//			long lens = f.length();//�����б�
			byte[] buffer = new byte[1024];
			dp.setData(buffer);

			DataInputStream ins = null;

			ins = new DataInputStream(new BufferedInputStream(new FileInputStream(msg)));

			int temp = 0;

			int n = 0;
			int c = 0;
			try {
				while ((n = ins.read(buffer)) != -1) {

					Usocket.send(dp);

					TimeUnit.MICROSECONDS.sleep(1);

				}
			} catch (IOException | InterruptedException e) {
				
				e.printStackTrace();
			}

			dp.setData("\n".getBytes());// �ļ���ȡ��Ϸ���һ��end��Ϣ

			try {
				Usocket.send(dp);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			break;
		}

	}

}
