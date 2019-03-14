package rface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * ��Ϣϵͳ�ӿ�
 *
 * @author Hanxy
 * @version 1.0.0
 * @see java.rmi.Remote
 */
public interface MessageInterface extends Remote{

    /**
     * ע���û�
     *
     * @param username �û���
     * @param password ����
     * @return ע���Ƿ�ɹ�
     * @throws RemoteException
     */
    public String register(String username, String password) throws RemoteException;

    /**
     * ��ʾ����ע���û�
     *
     * @return ����ע���û����б�
     * @throws RemoteException
     */
    public String showUsers() throws RemoteException;

    /**
     * ��ʾ�û���������
     *
     * @param username �û���
     * @param password ����
     * @return �û����������б�
     * @throws RemoteException
     */
    public String checkMessage(String username, String password) throws RemoteException;

    /**
     * ����
     *
     * @param username �û���
     * @param password ����
     * @param receiver_name ������
     * @param message_txt ������Ϣ
     * @return ���Է�����Ϣ
     * @throws RemoteException
     */
    public String leaveMessage(String username, String password, String receiver_name, String message_txt) throws RemoteException;
}
