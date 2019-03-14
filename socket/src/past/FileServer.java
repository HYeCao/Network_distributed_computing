package past;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

    static int TCPport = 2021;


    public static void main(String[] argc) throws IOException {
        int clientCount = 0;
        Socket Res_client ;
        ServerSocket server = new ServerSocket(TCPport);
        boolean flag = true;
        while(flag){
            Res_client = server.accept();
            clientCount++;
            System.out.println("与客户端连接成功");
            System.out.println("客户端地址为："+Res_client.getInetAddress());
            System.out.println("目前有"+clientCount+"个客户端与服务器建立链接");
            new Thread(new ServerThread(Res_client,clientCount)).start();
        }
        server.close();
    }
}
