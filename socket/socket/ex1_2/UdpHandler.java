package ex1_2;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpHandler implements Runnable {

	private DatagramSocket datagramSocket;
	private static final int MAX = 1024;

	public UdpHandler(DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}

	@Override
	public void run() {
		while (true) {
			try {
				DatagramPacket receivePacket = new DatagramPacket(null, MAX);
				datagramSocket.receive(receivePacket);
				String msg = new String(receivePacket.getData(), receivePacket.getLength());
				File sendFile = new File(msg);
				if (sendFile.isFile()) {
					byte[] sendBuffer = new byte[MAX];
					int size = 0;
					DatagramPacket sendPacket = new DatagramPacket(sendBuffer, MAX);
					BufferedInputStream bufferedInputStream = new BufferedInputStream(null);
					while ((size = bufferedInputStream.read(sendBuffer)) > 0) {
						sendPacket.setData(sendBuffer, 0, size);
						datagramSocket.send(sendPacket);
						Thread.sleep(10);
					}
					bufferedInputStream.close();
					sendPacket.setData("".getBytes(), 0, 0);
					datagramSocket.send(sendPacket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
