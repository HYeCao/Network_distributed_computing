package server;

import rface.MessageInterface;
import rface.MessageInterfaceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * RMI ������
 *
 * @author Hanxy
 * @version 1.0.0
 */
public class RMIServer {
    /**
     * ���� RMI ע����񲢽��ж���ע��
     */
    public static void main(String[] args) {
        try {
            // ����RMIע�����ָ���˿�Ϊ1099����1099ΪĬ�϶˿ڣ�
            // Ҳ����ͨ������ ��java_home/bin/rmiregistry 1099����
            // ���������ַ�ʽ�������ٴ�һ��DOS����
            // ����������rmi registry����ע����񻹱���������RMIC����һ��stub��Ϊ������
            LocateRegistry.createRegistry(1099);

            // ����Զ�̶����һ������ʵ����������MessageInterfaceImpl����
            // �����ò�ͬ����ע�᲻ͬ��ʵ��
            MessageInterface messageInterface = new MessageInterfaceImpl();

            // ��Messageע�ᵽRMIע��������ϣ�����ΪMessage
            Naming.rebind("Message", messageInterface);

            // ���Ҫ��helloʵ��ע�ᵽ��һ̨������RMIע�����Ļ�����
            // Naming.rebind("//192.168.1.105:1099/Hello",hello);
            System.out.println("Hello Server is ready.");
        } catch (Exception e) {
            System.out.println("Hello Server failed: " + e);
        }
    }
}
