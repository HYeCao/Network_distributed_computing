package ex2_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    /**
     * default HTTP port is port 80
     */
    static int PORT;
    /**
     * serverSocket will deal with the connection
     */
    static ServerSocket serverSocket;
    /**
     * ThreadPool,control the max connection number
     */
    static ExecutorService fixedThreadPool;
    /**
     * client socket to client
     */
    static Socket socket;

    /**
     * init all the param
     */
    public static void init(){
        PORT = 80;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 创建线程池并限定最大连接数量数量
         */
        fixedThreadPool =Executors.newFixedThreadPool(3);
        System.out.println("服务器初始化完成...");
    }

    public static void main(String[] args) throws IOException {
        init();
        while(true) {
            socket = serverSocket.accept();
            fixedThreadPool.execute(new ServerHandler(socket));
        }
    }

}
