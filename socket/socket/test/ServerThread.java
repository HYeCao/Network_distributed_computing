package test;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class ServerThread implements Runnable{
    private final String root = "D:/新建文件夹/新建文件夹/java/java";
    private String currentPath = root;
    private Socket Res_client = null;
    private int count = 0;
    private PrintStream out;
    private BufferedReader bf;
    private File folder;
    static int UDPport = 2020;
    DatagramSocket UDPsocket;


    public ServerThread(Socket client, int count){
        this.Res_client = client;
        this.count = count;
        folder = new File(root);
        try {
            UDPsocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try{
            System.out.println("创建新线程成功\n\0");
            out = new PrintStream(Res_client.getOutputStream());
            bf = new BufferedReader(new InputStreamReader(Res_client.getInputStream()));
            out.println(Res_client.getInetAddress()+" : "+Res_client.getPort()+" > 连接成功\n\0");
            boolean connection = true;
            while(connection){
                String str = bf.readLine();
                if(str.equals("bye")){
                    connection = false;
                    bye();
                }
                else if(str.equals("ls")){
                    ls();
                }
                else if(str.equals("cd..")||str.equals("cd ..")){
                    cdPP();
                }
                else {
                    StringTokenizer stringTokenizer = new StringTokenizer(str," ");
                    int part = stringTokenizer.countTokens();                   //输入字符串长度
                    if(part != 2){
                        out.println("unknown cmd\n\0");                     //当输入命令不是ls和cd..时，则为无效命令
                    }
                    else{
                        String first = stringTokenizer.nextToken();     //解析出第一部分内容
                        if(first.equals("cd")){
                            String goPath = stringTokenizer.nextToken();    //解析出第二部分
                            cd(goPath);
                        }
                        else if(first.equals("get")){
                            String FileName = stringTokenizer.nextToken();
                            if(judgeIsFile(FileName)){
                                downloadFile(FileName);
                            }
                            else{
                                out.println("unknown file\n\0");
                            }
                        }
                        else{
                            out.println("unknown cmd\n\0");
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //响应输入cd..
    private void cdPP(){                          //响应cd..
        if(currentPath.equals(root)){                       //如果是根目录，则无法继续向上
            out.println("this is root catalog\n"+currentPath+" > OK\n\0");
        }
        else{                                               //如果不是根目录，则向上返回
            String OldPath = currentPath;
            StringTokenizer stnr = new StringTokenizer(OldPath,"/");        //用StringTokenizer以/为分界对路径进行切割
            String Path = "";                                                   //Path记录上级目录
            int countTokens = stnr.countTokens();                               //计算共有几个切割结果
            for(int i = 0 ; i < countTokens - 1 ; i++){                         //保留除去最后一项的路径，即可得到当前目录的上层目录
                Path = Path + stnr.nextToken() + "/";
            }
            currentPath = Path;                                                 //更新当前目录
            folder = new File(currentPath);
            out.println(currentPath + " > OK\n\0");                                 //向客户端发送成功返回上层目录的信息
        }
    }

    private void cd(String goPath){
        StringTokenizer judgePath = new StringTokenizer(goPath,":");
        if(judgePath.countTokens() == 1){
            //cd相对路径
            boolean isFolderExist = false;
            for(File targetFolder: folder.listFiles() ){
                if(targetFolder.getName().equals(goPath)){
                    isFolderExist = true;
                }
            }
            if(isFolderExist){
                currentPath += goPath +"/";
                folder = new File(currentPath);
                out.println(currentPath+" > OK\n\0");
            }
            else{
                out.println(goPath+"is not a folder in "+currentPath+"\n\0");
            }
        }
        else {
            //cd绝对路径
            //如果输入正确，则可以进行cd操作，而且goPath相对root的层深度为goPathDepth-currentPathDepth
            if (judgeOutOfRoot(goPath)) {
                StringTokenizer goPathDepth = new StringTokenizer(goPath, "/");
                StringTokenizer currentPathDepth = new StringTokenizer(currentPath, "/");
                int depth = goPathDepth.countTokens() - currentPathDepth.countTokens();

                for (int i = 0; i < currentPathDepth.countTokens(); i++) {
                    goPathDepth.nextToken();
                }
                String FileName = goPathDepth.nextToken();
                boolean isFolderExist = false;
                for (int i = depth; i > 0; i--) {
                    for (File filesList : folder.listFiles()) {
                        if (filesList.getName().equals(FileName)) {
                            isFolderExist = true;
                            break;
                        }
                    }
                    if (isFolderExist) {
                        currentPath += "/" + FileName + "/";
                        folder = new File(currentPath);
                    } else {
                        out.println(FileName + " is not exist !\n" + currentPath + " > \n\0");
                        return;
                    }
                }
                out.println(goPath + " > OK\n\0");
            }
        }
    }

    private boolean judgeOutOfRoot(String goPath){
        //保证客户端输入的goPath在root目录下，不能跳出root目录
        StringTokenizer rootToken = new StringTokenizer(root,"/");
        StringTokenizer goPathToken = new StringTokenizer(goPath,"/");
        int rootPathDepth = rootToken.countTokens();
        int goPathDepth = goPathToken.countTokens();
        if(goPathDepth < rootPathDepth){            //当goPath划分的段数小于root划分段数，则路径一定是错误的
            out.println("the input path should be a catalog under root catalog\nthe root catalog is"+root+" > \n\0");
            return false;
        }
        else {
            while (rootToken.hasMoreTokens()) {           //当root用/分割后还有子目录时则继续循环
                if (!rootToken.nextToken().equals(goPathToken.nextToken())) {
                    //当root的下一个子目录与goPath下一个子目录不同时,则goPath不在root路径下，返回false
                    out.println("the input path should be a catalog under root catalog\nthe root catalog is" + root + " > \n\0");
                    return false;
                }
            }
        }
        return true;
    }

    private void ls(){
        String allFile = new String();
//        File[] next = folder.listFiles();
//        if (next != null) {
        for (File listOfFile : folder.listFiles()) {
            if (listOfFile.isDirectory()) {
                long size = getDirSize(listOfFile,0);
                allFile += "<dir>  " + listOfFile.getName() + "  " + size/1024 + "KB\n";
            } else if (listOfFile.isFile()) {
                long size = listOfFile.length();
                allFile += "<File>  " + listOfFile.getName() + "  " + size/1024 + "KB\n";
            }
        }
        //添加标识符，以确定客户端输出循环终止条件
        allFile += "\n\0";
        out.println(allFile);
        
    }

    //响应输入bye
    private void bye(){
        System.out.println("连接关闭");
        System.out.println("目前还有"+--count+"个客户端与服务器相连");
        try {
            Res_client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
        UDPsocket.close();
    }
    /*
     * @param folder:当前文件夹
     * @param size:记录文件的大小
     * return : 每个文件的大小
     */
    public long getDirSize(File folder, long size){
        for(File FileList:folder.listFiles()){
            if(FileList.isFile()){
                size += FileList.length();
            }
            else if(FileList.isDirectory()){
                size += getDirSize(FileList, size);
            }
        }
        return size;
    }

    public boolean judgeIsFile(String FileName){
        for(File FileList : folder.listFiles()){
            if(FileList.getName().equals(FileName)){
                return true;
            }
        }
        return false;
    }
    //响应输入get FileName
    public void downloadFile (String FileName)throws IOException{

        try {
            boolean isFile = false;
            for(File FileList : folder.listFiles()){
                if(FileList.getName().equals(FileName)){
                    isFile = true;
                    FileInputStream in = new FileInputStream(currentPath+FileList.getName());
                    long size = FileList.length();
                    out.println("开始传送文件");
                    out.println(currentPath+FileList.getName()+"-"+size);
                    byte[] cache = new byte[8192];
                    int len;
                    int loopTime = (int)size/8192;
                    int lastSize = (int)size%8192;
                    out.println(loopTime+"-"+lastSize);
                    while((len = in.read(cache)) != -1){
                        if (size >= 8192) {
                            DatagramPacket packet = new DatagramPacket(cache,8192,Res_client.getInetAddress(),2020);
                            UDPsocket.send(packet);
                        }
                        else{
                            int last = (int) size;
                            DatagramPacket packet = new DatagramPacket(cache,last,Res_client.getInetAddress(),2020);
                            UDPsocket.send(packet);
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        size -= 8192;
                        if(size <= 0)
                            break;
                    }

                }
                if(isFile)
                    break;
            }
            if(!isFile){
                out.println("unknown file\n\0");
            }
        } catch (FileNotFoundException e) {
            out.println("unknown file\n\0");
        }
    }
}
