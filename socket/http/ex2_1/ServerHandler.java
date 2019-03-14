package ex2_1;

import java.io.*;

import java.net.Socket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerHandler implements Runnable {
    private String CRLF = "\r\n";

    /**
     *  接收传入的Socket，处理会话
     */
    private Socket         client;
    /**
    * 服务器接收客户端的输入流
     */
    private BufferedReader read;
    /**
    * 服务器回应客户端的输出流
     */
    private BufferedOutputStream    bos;
    /**
    * 服务器根目录
     */
    String root;

    /**
    * @param socket: is the client socket to connect
    * 初始化数据,其中read用于读取客户端输入，bos发送响应，root为服务器根目录
     */
    public ServerHandler(Socket socket) throws IOException {
        this.client = socket;
        read        = new BufferedReader(new InputStreamReader(client.getInputStream()));
        bos         = new BufferedOutputStream(client.getOutputStream());
        root = "D:/images";
    }

    /**
    * @param request: is the commend client sent to server
    * @return boolean: is the commend HTTP's version is HTTP/1.1
    * 判断命令是否为HTTP/1.1版本的命令，是否在头部包含了Host信息
     */
    public boolean judgeVersion11(String request) {
        //如果有Host信息，则符合HTTP1.1版本的要求
        if ( request.contains("Host :") ) {
            return true;
        }
        return false;
    }

    /**
    * @param request:  is the comment client sent to server
    * 解析get命令中的文件名并在服务器中找到这个文件，如果找到，则返回编码为200 Ok的报文以及文件信息
    * 如果没有找到，则返回编码为404 Not Found的报文
    * 如果无法解析get命令，则返回编码为400 Bad Request的报文
     */
    public void get(String request){
        String fileName = getFileName(request);
        File file = null;
        if (fileName.equals("/")) {
            file = new File(root + "/index.html");
        } else {
            file = new File(root + fileName);
        }
        if (file.isFile()) {
            long fileSize = file.length();//得到文件大小
            DataInputStream bis = null;
            try {
                bis = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            respond200(fileSize);
            byte[] buffer = new byte[4*1024];
            try {
                int len;
                while ((len = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, buffer.length);
                    bos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bos.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            respond404();
        }

    }

    /**
    * @param fileSize: size of the file
    * 向客户端回复编码为200 ok的报文，并返回相应信息
     */
    public void respond200(long fileSize){
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //取得当前时间
        String time = format.format(date);
        String response = "HTTP/1.1 200 OK" + CRLF +
                "Date:" + time + CRLF +
                "Content-Type: text/html" + CRLF +
                "Content-Length: " + fileSize + CRLF + CRLF;
        try {
            bos.write(response.getBytes());
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * 向客户端回复编码为400 Bad Request的报文，并返回相应信息
     */
    public void respond400(){
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //取得当前时间
        String time = format.format(date);
        String response = "HTTP/1.1 400 Bad Request"+ CRLF +
                "Date: "+ time + CRLF +
                "Server: 127.0.0.1" + CRLF
                + "Connection: close" + CRLF
                + "Content-Length: ";
        int t = response.length();
        response += t + CRLF + CRLF;
        try {
            bos.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
    * 向客户端回复编码为404 Not Found的报文，并返回相应信息
     */
    public void respond404(){
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //取得当前时间
        String time = format.format(date);
        String response = "HTTP/1.1 404 Not Found" + CRLF
                + "Date: "+ time + CRLF +
                "Server: 127.0.0.1" + CRLF
                + "Connection: close" + CRLF
                + "Content-Length: ";
        int t = response.length();
        response += t + CRLF + CRLF;
        try {
            bos.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
    * 线程运行入口
     */
    @Override
    public void run() {
        //request 接收client的信息
        String request = null;

        //判断输入是否为合法命令
        boolean legalRequest = false;
        try {
            request = read.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果命令以GET开头
        if (request.startsWith("GET")) {
            //判断命令是否符合HTTP1.1版本的规范
            if (request.contains("HTTP/1.1")) {
                legalRequest = judgeVersion11(getHeader(request));
            }
            else {
                legalRequest = true;
            }
            if(legalRequest){
                //如果是合法输入，则GET
                get(request);
            }
            else{
                //如果输入非法，则返回400
                respond400();
            }
        } else if (request.startsWith("PUT")) {
            //判断命令是否符合HTTP1.1版本的规范
            if(request.contains("HTTP/1.1")){
                legalRequest = judgeVersion11(getHeader(request));
            }
            else {
                legalRequest = true;
            }
            if(legalRequest){
                //如果输入合法，则PUT
                put(request);
            }
            else {
                //如果输入非法，则返回400
                respond400();
            }
        }
    }

    /**
    * @param request: request is the comment client sent to server
    * @return fileName: name of file in commend
    * 获取命令中的文件名，并返回
     */
    public String getFileName(String request){
        String[] split = request.split(" ", 3);
        String fileName = split[1];
        return fileName;
    }
    /**
    * @param request: request is the comment client sent to server
    * 解析PUT命令中的文件路径，读取命令中body文件信息存储到新建的文件中
    *
    * 目前bug： 1、PUT的respond200的响应客户端接收不到
    *           2、接收文件乱码，不会调
     */
    public void put(String request){


        //获取文件路径
        String fileName = getFileName(request);
        //新建文件
        File file = new File(root+fileName);

        FileOutputStream fileOutputStream;

        byte[] buffer = new byte[8196];
        BufferedInputStream istream = null;
        try {
            istream = new BufferedInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer header = new StringBuffer();
        StringBuffer response = new StringBuffer();

        int last = 0, c = 0;
        /**
         * Process the header and add it to the header StringBuffer.
         */
        boolean inHeader = true; // loop control
        try {
            System.out.println("开始接收Header");
            while (inHeader && ((c = istream.read()) != -1)) {
                switch (c) {
                    case '\f':
                        break;
                    case '\n':
                        if (c == last) {
                            inHeader = false;
                            break;
                        }
                        last = c;
                        header.append("\n");
                        break;
                    default:
                        last = c;
                        header.append((char) c);
                }
            }
            System.out.println("Header接收完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Read the contents and add it to the response StringBuffer.
         */
        try {
            System.out.println("开始接收Body");
            fileOutputStream = new FileOutputStream(file);
            while (read.ready()){//istream.read(buffer) != -1) {
                response.append(new String(buffer,"iso-8859-1"));
                fileOutputStream.write(buffer);
            }
            System.out.println("Body接收完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //发送对应编码为200 ok的信息
        respond200(file.length());
    }

    /**
    * @param request:request is the comment client sent to server
    * @return header:commend's header
     */
    public String getHeader(String request) {
        String[] split = request.split(CRLF + CRLF);
        String          header          = split[0];
        return header;
    }
}

