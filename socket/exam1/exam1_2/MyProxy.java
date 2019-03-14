package exam1_2;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class MyProxy extends Thread {

    Socket socket ;//���socket������߳����������socket

    String targetHost=null;
    String targetPort;
    InputStream inputStream_client;//���������������ȡ�����������������
    OutputStream outputStream_client;//�����������������ݷ��͵������
    PrintWriter outPrintWriter_client;//���writer�����������д������
    BufferedReader bufferedReader_client;//��������������������������

    Socket accessSocket;//���socket��������վ����

    InputStream inputStream_Web;//���������������ȡ����վ���ص���Ӧ
    OutputStream outputStream_Web;//����������������վ��������
    PrintWriter outPrintWriter_Web;//���writer��������վ��������
    BufferedReader bufferedReader_web;//�������������������վ���͵�����

    String cacheFilePath;
    File file=null;
    FileInputStream fileInputStream;
    String url="";
    ArrayList<String>cache;
    int cache_url_index=-1;
    boolean has_cache_no_timestamp=false;

    public MyProxy(Socket inputSocket) throws IOException {
        socket=inputSocket;
        /** ����һ���ļ����� */
        file=new File(HttpProxy.cachePath);
        if (!file.exists()){//�ļ����������½�һ���ļ�
            file.createNewFile();
        }

        fileInputStream=new FileInputStream(HttpProxy.cachePath);

        System.out.print("�������������\n");
        System.out.print("��ȡ��socket����"+inputSocket.getInetAddress()+":"+inputSocket.getPort()+"\n");

        inputStream_client=socket.getInputStream();//�������������ȡ�����������
        bufferedReader_client=new BufferedReader(new InputStreamReader(inputStream_client));
        outputStream_client=socket.getOutputStream();//�����������������Ӧ����
        outPrintWriter_client=new PrintWriter(outputStream_client);
        /** ��ȡ���� */
        cache=readCache(fileInputStream);
        System.out.println("�����Ļ�����"+cache.size()+"��");

        start();//�������߳�
    }
    public void run() {
        try {
            socket.setSoTimeout(HttpProxy.TIMEOUT);//�������ȴ�ʱ�䣬�������Զ��Ͽ�����
            String buffer;
            //debug
            System.out.println("���������ȡ��һ��....");
            buffer = bufferedReader_client.readLine();//���������ȡ��һ������
            System.out.println(buffer);


            /** ��ȡ url */
            url=getURL(buffer);
            /** ����һЩ���ҵ����󣬱���Google�ĺ�һЩ��̨��CONNECT������QQ�ܼҵļ��� */
            if(buffer.contains("CONNECT")||buffer.contains("google")||buffer.contains("c.gj.qq.com")){
                System.out.println("����"+buffer+"�ѱ�����");
                return ;//�˳�run()���������߳̾��Զ�����
            }

            /** ������д�뻺���ļ�,����������Ѿ�����ͬ�����󣬾Ͳ���д���� */
            boolean has_in_cache_already=false;
            for(String iter:cache){
                if (iter.equals(buffer)) {
                    has_in_cache_already = true;
                    break;
                }
            }
            if (has_in_cache_already==false){
                String temp = buffer + "\r\n";
                write_cache(temp.getBytes(), 0, temp.length());
            }

            /** ��ȡ�����Ͷ˿� */
            String[] HostandPort=new String[2];
            if (buffer!=null)
             HostandPort= findHostandPort(buffer);
            targetHost = HostandPort[0];
            targetPort = HostandPort[1];

            System.out.println("��ȡ��������:" + targetHost + " ��ȡ�Ķ˿ں�: " + targetPort);

            /** ������Ŀ���������� */
            int retry = HttpProxy.RETRIEVE;
            while (retry-- != 0 && (targetHost != null)) {
                try {
                    accessSocket = new Socket(targetHost, Integer.parseInt(targetPort));
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(HttpProxy.CONNECT_PAUSE);//�ȴ�
            }
            if (accessSocket != null) {//�ɹ���������
                //debug
                System.out.println("���󽫷�����:" + targetHost);
                accessSocket.setSoTimeout(HttpProxy.TIMEOUT);
                inputStream_Web = accessSocket.getInputStream();//��ȡ��վ���ص���Ӧ
                bufferedReader_web = new BufferedReader(new InputStreamReader(inputStream_Web));
                outPrintWriter_Web = new PrintWriter(accessSocket.getOutputStream());//׼��������վ��������


                 /** ��������ļ�Ϊ�� */
                if (cache.size()==0) {
                    /** ������ֱ�ӷ�����վ������ȡ��Ӧ����¼��Ӧ������ */
                    sendRequestToInternet(buffer);
                    transmitResponseToClient();
                } else {//�����ļ���Ϊ�գ�Ѱ��֮ǰ��û�л����������
                    String modifyTime;
                    String info="";
                    modifyTime=findModifyTime(cache,buffer);//��ȡmodifytime
                    System.out.println("��ȡ����modifytime��"+modifyTime);
                    if (modifyTime!=null||has_cache_no_timestamp){
                        /** �����������������������û��Last-Modify���Եģ��Ͳ������������ѯIf-Modify�ˣ��������������ѯIf-Modify */
                        if (!has_cache_no_timestamp){
                            buffer += "\r\n";
                            outPrintWriter_Web.write(buffer);
                            System.out.print("�����������ȷ���޸�ʱ������:\n" + buffer);
                            String str1 = "Host: " + targetHost + "\r\n";
                            outPrintWriter_Web.write(str1);
                            String str = "If-modified-since: " + modifyTime
                                    + "\r\n";
                            outPrintWriter_Web.write(str);
                            outPrintWriter_Web.write("\r\n");
                            outPrintWriter_Web.flush();
                            System.out.print(str1);
                            System.out.print(str);

                             info= bufferedReader_web.readLine();
                            System.out.println("���������ص���Ϣ�ǣ�" + info);
                        }

                        if (info.contains("Not Modified")||has_cache_no_timestamp) {//������������ص���Ӧ��304 Not Modified���ͽ����������ֱ�ӷ��͸������
                            int contentindex = 0;
                            String temp_response="";
                            System.out.println("ʹ�û�������");
                            if (cache_url_index!=-1)
                            for (int i=cache_url_index+1;i<cache.size();i++){
                                if (cache.get(i).contains("http://"))
                                    break;
                                temp_response+=cache.get(i);
                                temp_response+="\r\n";

                            }
                            System.out.println("ʹ�û��棺\n"+temp_response);
                            outputStream_client.write(temp_response.getBytes(),0,temp_response.getBytes().length);
                            outputStream_client.write("\r\n".getBytes(),0,"\r\n".getBytes().length);
                            outputStream_client.flush();
                        } else {
                            /** ���������صĲ���304 Not Modified�Ļ����ͽ�����������Ӧֱ��ת�������������¼����ͺ��� */
                            System.out.println("�и��£�ʹ���µ�����");
                            transmitResponseToClient();
                        }
                    }else{
                        /**������û���ҵ�֮ǰ�ļ�¼��ֱ�ӽ������͸���վ����������Ӧ������Ӧд�뻺�� */
                        sendRequestToInternet(buffer);
                        transmitResponseToClient();
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *�������͸���վ
     * @param buffer ����ĵ�һ�б���
     * @throws IOException
     */
    private void sendRequestToInternet(String buffer) throws IOException {
        while(!buffer.equals("")){
            buffer+="\r\n";
            outPrintWriter_Web.write(buffer);
            System.out.print("��������:"+buffer+"\n");
            buffer=bufferedReader_client.readLine();
        }
        outPrintWriter_Web.write("\r\n");
        outPrintWriter_Web.flush();
    }

    /**
     * ��ȡ�������Ͷ˿�
     * @param content ����ȡ�ı��ģ���������ĵ�һ��
     * @return
     */
    private String[] findHostandPort(String content){
        String host=null;
        String port=null;
        String[] result=new String[2];
        int index;
        int portIndex;
        String temp;

        StringTokenizer stringTokenizer=new StringTokenizer(content);
        stringTokenizer.nextToken();//������һ���ִ� ������������ ����GET POST
        temp=stringTokenizer.nextToken();//����ִ��������������Ͷ˿�

        host=temp.substring(temp.indexOf("//")+2);//���� http://news.sina.com.cn/gov/2017-12-13/doc-ifypsqiz3904275.shtml -> news.sina.com.cn/gov/2017-12-13/doc-ifypsqiz3904275.shtml
        index=host.indexOf("/");
        if (index!=-1){
            host=host.substring(0,index);//���� news.sina.com.cn/gov/2017-12-13/doc-ifypsqiz3904275.shtml -> news.sina.com.cn
            portIndex=host.indexOf(":");
            if (portIndex!=-1){
                port=host.substring(portIndex+1);//���� www.ghostlwb.com:8080 -> 8080
                host=host.substring(0,portIndex);
            }else{//û���ҵ��˿ںţ������Ĭ�϶˿ں�80
                port="80";
            }
        }
        result[0]=host;
        result[1]=port;
        return result;
    }

    /**
     * ��ȡURL
     * @param firstline �����ĵĵ�һ��
     * @return
     */
    private String getURL(String firstline){
        StringTokenizer stringTokenizer=new StringTokenizer(firstline);
        stringTokenizer.nextToken();
        return stringTokenizer.nextToken();
    }

    /**
     * ��������������£�����վ������Ӧ�����͸��������������Ӧд�뻺��
     * @throws IOException
     */
    private void transmitResponseToClient() throws IOException {

        byte[] bytes=new byte[2048];
        int length=0;

        while(true){
            if((length=inputStream_Web.read(bytes))>0){
                outputStream_client.write(bytes,0,length);
                String show_response=new String(bytes,0,bytes.length);
                System.out.println("���������ص���Ϣ��:\n---\n"+show_response+"\n---");
                write_cache(bytes,0,length);
                write_cache("\r\n".getBytes(),0,2);
                continue;
            }
            break;
        }

        outPrintWriter_client.write("\r\n");
        outPrintWriter_client.flush();
    }

    /**
     * ���ļ��ж�ȡ�������ݣ����ж�ȡ
     * @param fileInputStream
     * @return
     */
    private ArrayList<String> readCache(FileInputStream fileInputStream){
        ArrayList<String> result=new ArrayList<>();
        String temp;
        BufferedReader br=new BufferedReader(new InputStreamReader(fileInputStream));
        try {
            while((temp=br.readLine())!=null){
                result.add(temp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ������д�뻺�棬�����δ���ο����ϵ�
     * @param c
     * @throws IOException
     */
    private void write_cache(int c) throws IOException {
            HttpProxy.writeCache.write((char) c);
    }

    private void write_cache(byte[] bytes, int offset, int len)
            throws IOException {
        for (int i = 0; i < len; i++)
            write_cache((int) bytes[offset + i]);
    }

    /**
     * ��ȡmodifytime
     * @param cache_temp
     * @param request
     * @return
     */
    private String findModifyTime(ArrayList<String> cache_temp,String request){
        String LastModifiTime=null;
        int startSearching=0;
        has_cache_no_timestamp=false;

        System.out.println("��Ҫ�ȶԵ�URL��"+request);
        for(int i=0;i<cache_temp.size();i++){

            if (cache_temp.get(i).equals(request)){
                startSearching=i;
                cache_url_index=i;
                for(int j=startSearching+1;j<cache_temp.size();j++){
                    if(cache_temp.get(j).contains("http://"))
                        break;
                    if (cache_temp.get(j).contains("Last-Modified:")){
                        LastModifiTime=cacheFilePath.substring(cache_temp.get(j).indexOf("Last-Modified:"));
                        return LastModifiTime;
                    }
                    if (cache_temp.get(j).contains("<html>")){
                        has_cache_no_timestamp=true;
                        return LastModifiTime;
                    }
                }
            }
        }

        return LastModifiTime;
    }

}
