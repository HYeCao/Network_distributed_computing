package past;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class FileClient {
    private static DatagramSocket dataSocket;
    private static DatagramPacket dataPacket;
    private static BufferedReader bf;

    public static void main(String[] argc) throws IOException{

        String serverIP = "127.0.0.1";
        Socket client = new Socket(serverIP,2021);
        //获取键盘输入
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        //获取Socket的输入流，接收服务器发过来的数据
        bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //获取Socket的输出流，用来发送数据到服务器
        PrintStream pw = new PrintStream(client.getOutputStream());
        boolean flag = true;
        System.out.println(bf.readLine());
        String echo = bf.readLine();
        System.out.println(echo);
        while(flag){
            String str = input.readLine();
            pw.println(str);
            if (str.equals("bye")) {
                flag = false;
            } else {
                try {
                    String respond = bf.readLine();

                    while (!respond.equals("\0")){
                        System.out.println(respond);

                        if(respond.contains("开始传送文件")){
                            respond = bf.readLine();
                            //System.out.println(respond);
                            String[] splitSize = respond.split("-");
                            System.out.println("size:"+splitSize[1]);
                            String[] splitFileName = splitSize[0].split("/");
                            String fileName = splitFileName[splitFileName.length-1];
                            System.out.println("fileName:"+fileName);
                            int size = Integer.parseInt(splitSize[1]);
                            System.out.println(size);
                            try {
                                get(fileName,size);
                                pw.println(input.readLine());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else if(respond.contains("unknown file")){
                            System.out.println(respond);
                        }
                        respond = bf.readLine();
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Time out, No response");
                }
            }
        }
        input.close();
        client.close();
    }

    private static void get(String fileName, int size) throws Exception {

        String path ="D:/";

        System.out.println("开始接收文件");
        dataSocket = new DatagramSocket(2020);//建立端口号为portUDP的udp连接

        //新建流进行处理接收的文件
        DataOutputStream fileOut = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(path + fileName)));
        byte[] receiveData = new byte[8192];//缓存数组
        int buffLen = receiveData.length;//缓存大小

        String respond = bf.readLine();//读不到


        String[] split = respond.split("-");
        System.out.println("loopTime:"+split[0]);
        System.out.println("lastSize:"+split[1]);
        int loopTimes =  Integer.parseInt(split[0]);//循环接收次数
        int lastSize = Integer.parseInt(split[1]);
        dataPacket = new DatagramPacket(receiveData, buffLen);
        for (int i = 0; i < loopTimes; i++) {//循环接收文件
            int time = i+1;
            try {
            //    dataSocket.setSoTimeout(2000);//设定超时限制
                dataSocket.receive(dataPacket);
                System.out.println("第"+time+"个片段接收成功");
            } catch (Exception e) {
                System.err.println("第"+time+"个片段接收超时");
            }
            fileOut.write(receiveData, 0, dataPacket.getLength());//写入预定的文件
            fileOut.flush();
            //System.out.println("one loop over");
        }
        //最后写入剩余的部分，即lastSize
        dataPacket = new DatagramPacket(new byte[lastSize],lastSize);
        int time = loopTimes +1;
        try {
            //dataSocket.setSoTimeout(2000);//设定超时限制
            dataSocket.receive(dataPacket);
            System.out.println("第"+ time +"个片段接收成功");
        } catch (Exception e) {
            System.err.println("第"+ time +"个片段接收超时");
        }
        fileOut.write(receiveData, 0, lastSize);//写入预定的文件
        fileOut.flush();


        fileOut.close();
        dataSocket.close();
        System.err.println("接受完毕");
    }
}
