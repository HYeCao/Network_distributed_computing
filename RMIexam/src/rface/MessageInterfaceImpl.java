package rface;

import bean.Message;
import bean.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * ��Ϣϵͳ�ӿ�ʵ��
 *
 * @author Hanxy
 * @version 1.0.0
 * @see ssd8.exam2.rface.MessageInterface
 * @see java.rmi.server.UnicastRemoteObject
 */
public class MessageInterfaceImpl extends UnicastRemoteObject implements MessageInterface {


    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Message> messages = new ArrayList<>();

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    /**
     * Ϊά�����㣬���з�����Ϣд�ڴ˴�
     */
    private static final String CRLF = "\r\n";
    private static final String REGISTER_SUCCESS = "ע��ɹ���";
    private static final String REGISTER_FAILURE = "�û����Ѵ��ڣ���ѡ����һ���û�����";
    private static final String LEAVE_SUCCESS = "���Գɹ���";

    private static final String ACCOUNT_ERROR = "�û������������";

    private static final String NO_USERS = "ϵͳ��δע���û�";

    private static final String NO_USER = "ϵͳû�и��û�";
    private static final String NO_MESSAGE = "û��������";

    /**
     * ���캯��
     *
     * @throws RemoteException
     */
    public MessageInterfaceImpl() throws RemoteException {
        super();
    }

    /**
     * ע���û�
     *
     * @param username �û���
     * @param password ����
     * @return ע���Ƿ�ɹ�
     * @throws RemoteException
     */
    @Override
    public String register(String username, String password) throws RemoteException {
        /*
        ע��ʧ��
         */
        if (isUserExist(username)){
            return REGISTER_FAILURE;
        }
        /*
        ע��ɹ�
         */
        User user = new User(username, password);
        users.add(user);
        return REGISTER_SUCCESS;
    }

    /**
     * ��ʾ����ע���û�
     *
     * @return ����ע���û����б�
     * @throws RemoteException
     */
    @Override
    public String showUsers() throws RemoteException {
        /*
        û���û�
         */
        if (users.isEmpty()){
            return NO_USERS;
        }else {//���û�
            String info = "";
            for (User user : users) {
                info += user.getUsername();
                info += CRLF;
            }
            return info;
        }
    }

    /**
     * ��ʾ�û���������
     *
     * @param username �û���
     * @param password ����
     * @return �û����������б�
     * @throws RemoteException
     */
    @Override
    public String checkMessage(String username, String password) throws RemoteException {
        String info = "";
        boolean hasMessage = false;
        /*
        �ж��û��˻�
         */
        if (!isUserCorrect(username, password)){
            return ACCOUNT_ERROR;
        }else {

            for (Message message : messages){
                if (message.getReceiverName().equals(username)){
                    hasMessage = true;
                    info += message.toString();
                    info += CRLF;
                }
            }
        }
        /*
        �ж��Ƿ�������
         */
        if (hasMessage){
            return info;
        }else return NO_MESSAGE;
    }

    /**
     * ����
     *
     * @param username      �û���
     * @param password      ����
     * @param receiver_name ������
     * @param message_txt   ������Ϣ
     * @return ���Է�����Ϣ
     * @throws RemoteException
     */
    @Override
    public String leaveMessage(String username, String password, String receiver_name, String message_txt) throws RemoteException {
        /*
        �ж��û��˺���������˺�
         */
        if (!isUserCorrect(username, password)){
            return ACCOUNT_ERROR;
        }else if (!isUserExist(receiver_name)){
            return NO_USER;
        }
        /*
        ����
         */
        Message message = new Message(username, receiver_name, dateFormat.format(new Date()), message_txt);
        messages.add(message);
        return LEAVE_SUCCESS;
    }

    private boolean isUserCorrect(String username, String password){
        User currentUser = new User(username, password);
        for (User user : users) {
            if (currentUser.equals(user)){
                return true;
            }
        }
        return false;
    }

    private boolean isUserExist(String username){
        for (User user: users){
            if (user.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }
}
