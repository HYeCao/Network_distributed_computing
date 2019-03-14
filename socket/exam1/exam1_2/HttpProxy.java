package exam1_2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class HttpProxy {
    public static String cachePath="";
    public static OutputStream writeCache;
    public static int TIMEOUT=5000;//response time out upper bound
    public static int RETRIEVE=5;//retry connection 5 times
    public static int CONNECT_PAUSE=5000;//waiting for connection
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket;
        Socket currsoket=null;
            /** users need to setup work space */

            System.out.println("==============�����뻺��Ĵ洢Ŀ¼������ d ������ΪĬ��Ŀ¼������ͬһĿ¼�£�=================");
            Scanner scanner=new Scanner(System.in);
            cachePath=scanner.nextLine();
            if(cachePath.equals("d")){
                cachePath="defaul_cache.txt";
            }
            /** ��ʼ������д���� */
            writeCache=new FileOutputStream(cachePath,true);
            System.out.println("=================================== ����Ŀ¼�������====================================");

        try {
            //����serversocket���󶨶˿�8888
            serverSocket=new ServerSocket(8888);
            int i=0;
            //ѭ������������������˿ڵ���������
            while(true){
                currsoket=serverSocket.accept();
                //����һ���µ��߳��������������
                i++;
                System.out.println("������"+i+"���߳�");
                new MyProxy(currsoket);
            }
        } catch (IOException e) {
            if (currsoket != null) {
                currsoket.close();//��ʱ�ر����socket
            }
            e.printStackTrace();
        }
        writeCache.close();//�ر��ļ������
    }
}
