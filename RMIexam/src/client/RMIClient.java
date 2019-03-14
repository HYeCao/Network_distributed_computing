package client;

import rface.MessageInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * RMI �ͻ���
 *
 * @author Hanxy
 * @version 1.0.0
 */
public class RMIClient {

    private static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Ϊά�����㣬���в��������Ϣд�ڴ˴�
     */
    private static final String WRONG_PARAMETER = "��������!";
    private static final String SUCCESS = "�����ɹ���";
    private static final String FAILURE = "����ʧ�ܣ�";
    private static final String TIME_FORMAT = "ʱ���ʽ��yyyy-MM-dd-HH:mm:ss";

    private static final String REGISTER_SUCCESS = "ע��ɹ���";
    private static final String REGISTER_FAILURE = "�û����Ѵ��ڣ���ѡ����һ���û�����";

    /**
     * ��ǰ�û���������
     */
    private static String username;
    private static String password;

    /**
     * RMI �ӿ�
     */
    static MessageInterface rmi;

    public static void main(String[] args) {
        /**
         * ����Զ�̶���
         */
        try {
            if (args.length < 3) {
                System.err.println(WRONG_PARAMETER);
                System.exit(0);
            }
            String host = args[0];
            String port = args[1];
            /*
            ͨ�����һ��Զ�̶���
             */
            rmi = (MessageInterface) Naming.lookup("//" + host + ":" + port + "/Message");

            /**
             * ע�����
             */
            if (args[2].equals("register")) {
                if (args.length != 5) {
                    System.err.println(WRONG_PARAMETER);
                    System.exit(0);
                }
                String info = rmi.register(args[3], args[4]);
                if (info.equals(REGISTER_FAILURE)) {
                    System.err.println(REGISTER_FAILURE);
                } else {
                    username = args[3];
                    password = args[4];
                    System.out.println(username + REGISTER_SUCCESS);
                }
            } else {//��������
                username = args[3];
                password = args[4];
                String[] cmds = Arrays.copyOfRange(args, 5, args.length);
                service(cmds);
            }
            /**
             * ��ʾ����
             */
            helpMenu();

            /**
             * ��������
             */
            while (true) {
                System.out.println("Input an operation:");
                String operation = bf.readLine();
                String[] cmds = operation.split(" ");
                service(cmds);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��������
     *
     * @param cmds ����
     */
    private static void service(String[] cmds) throws RemoteException {
        if (cmds[0].equals("show")){
            doShow();
        }else if (cmds[0].equals("check")){
            doCheck();
        }else if (cmds[0].equals("leave")){
            doLeave(cmds);
        }else if (cmds[0].equals("help")){
            helpMenu();
        }else if (cmds[0].equals("quit")){
            System.exit(0);
        }else System.err.println(WRONG_PARAMETER);
    }

    /**
     * ��ʾ�����˵�
     */
    private static void helpMenu() {
        System.out.println(TIME_FORMAT);
        System.out.println("HELP MENU:");
        System.out.println("\t" + "1.show");
        System.out.println("\t\t" + "arguments:no args");
        System.out.println("\t" + "2.check");
        System.out.println("\t\t" + "arguments:no args");
        System.out.println("\t" + "3.leave");
        System.out.println("\t\t" + "arguments:<receiver_name> <message_text>");
        System.out.println("\t" + "4.help");
        System.out.println("\t\t" + "arguments:no args");
        System.out.println("\t" + "5.quit");
        System.out.println("\t\t" + "arguments:no args");
    }

    private static void doShow() throws RemoteException {
        System.out.println(rmi.showUsers());
    }

    private static void doCheck() throws RemoteException {
        System.out.println(rmi.checkMessage(username, password));
    }

    private static void doLeave(String[] cmds) throws RemoteException {
        if (cmds.length != 3) {
            System.err.println(WRONG_PARAMETER);
        }else {
            System.out.println(rmi.leaveMessage(username, password, cmds[1], cmds[2]));
        }
    }
}
