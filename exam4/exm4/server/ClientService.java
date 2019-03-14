package exm4.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import exm4.server.bean.DoList;
import exm4.server.bean.User;

/**
 * �û�������
 * @author dell
 *
 */
@WebService(name="clientservice",  portName="clientserviceport", targetNamespace="http://www.exam4server.com")
public class ClientService {

	/**
	 * �����û��б�
	 */
	private List<User> userList;
	/**
	 * ͳһʱ���ʽ
	 */
	private SimpleDateFormat simpleDateFormat;
	/**
	 * ʱ���ʽ
	 */
	static final String dateFormat = "yyyy-MM-dd HH:mm";
	/**
	 * ���캯������
	 */
	public ClientService() {
		userList=new ArrayList<User>();
		simpleDateFormat=new SimpleDateFormat(dateFormat);
	}

	public ClientService(List<User> userList) {
		super();
		this.userList = userList;
	}
	/**
	 * ����������������������
	 * @param args
	 */
	public static void main(String[] args) {  
        Endpoint.publish("http://127.0.0.1:8002/webservice/clientservice", new ClientService());  
        //wsimport -keep http://127.0.0.1:8002/webservice/clientservice?wsdl
        System.out.println("����������");
    }
	/**
	 * ע���û���Ϣ
	 * @param userName ��Ҫע����û�������Ҫ��֮ǰ���û������ظ�
	 * @param password ��Ҫע����û�����
	 * @return ����ظ�����exist ���򷵻�ok
	 * ���û�����ע�ᡣ���û������ṩ�û��������롣
	 * ����ṩ���û������Ѿ����ڣ���ӡһ��������Ϣ��
	 * ע��ɹ��󣬴�ӡһ����Ϣ˵��ע��ɹ���
	 * �����û�ע��ʱ����ҪΪ������û�����һ���µĴ��������б����
	 */
	@WebMethod
	public boolean register(String userName, String password) {
		for (User user : userList) {
			if (user.getName().equals(userName)) {
				return false;
			}
		}
		User user = new User(userName, password);
		userList.add(user);
		return true;
	}


	/**
	 * ��Ӵ��������
	 * @param userName ��Ҫ��Ӵ���������û���
	 * @param startDateStr ��ʼʱ�� 
	 * @param endDateStr ����ʱ�� 
	 * @param title �����������
	 * @return �����ӳɹ�����true���򷵻�false
	 * @throws ParseException ���startDateStr��endDateStr��ʽ�����׳�����
	 */
	@WebMethod
	public boolean addlist(String userName, String startDateStr, String endDateStr, String title) throws ParseException {
		/**
		 * ��ʼʱ���Լ�����ʱ��Ĺ淶��
		 */
		Date startDate = simpleDateFormat.parse(startDateStr);
		Date endDate = simpleDateFormat.parse(endDateStr);
		if (isRightDateOrder(startDate, endDate)) {
			DoList backlog=new DoList(startDate, endDate, title);
			for (User user : userList) {
				if (user.getName().equals(userName)) {
					user.getBacklogList().add(backlog);
					return true;
				}
			}
		} 
		return false;
	}

	/**
	 * ��ѯ��������
	 * @param userName     ��Ҫ���в�ѯ���û���
	 * @param startDateStr ��ʼʱ�� 
	 * @param endDateStr   ����ʱ�� 
	 * @return             ����û������ڷ��ز�ѯ������Ϣ�����򷵻�fail
	 * @throws ParseException ���startDateStr��endDateStr��ʽ�����׳�����
	 */
	@WebMethod
	public String querylist(String userName, String startDateStr, String endDateStr) throws ParseException {
		Date startDate = simpleDateFormat.parse(startDateStr);
		Date endDate = simpleDateFormat.parse(endDateStr);
		if (isRightDateOrder(startDate, endDate)) {
			String result = "";
			for (User user : userList) {
				if (user.getName().equals(userName)) {
					List<DoList> backlogs=new ArrayList<DoList>() ;
					for (DoList backlog : user.getBacklogList()) {
						if (!backlog.noContainDates(startDate, endDate)) {
							backlogs.add(backlog);
						}
					}
					result = sortlist(backlogs);
					return result;
				}
			}
		}
		return "fail";
	}
	
	/**
	 * �����û��������Ƿ���ȷ����
	 * @param userName �û���
	 * @param password ����
	 * @return �����ȷ����true���򷵻�false
	 * 
	 */
	@WebMethod
	public boolean isright(String userName, String password) {
		for (User user : userList) {
			if (user.getName().equals(userName)) {
				if (user.getPassword().equals(password)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	/**
	 * ע���û����Դ����ǵĴ��������б���ɾ����Ŀ�������ǽ���ɾ������Ŀ��ID
	 * @param userName �û���
	 * @param backlogId ��Ҫɾ�����������id
	 * @return ɾ���ɹ�����true���򷵻�false
	 */
	@WebMethod
	public boolean deletelist(String userName, String backlogId) {
		for (User user : userList) {
			if (user.getName().equals(userName)) {
				List<DoList> backlogs=user.getBacklogList();
				for (DoList backlog : backlogs) {
					if(backlog.getBacklogId().equals(backlogId))
					{
						backlogs.remove(backlog);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * ע���û�������մ��������б��е�������Ŀ
	 * @param userName ��Ҫ�������������û���
	 * @return ������ɹ�����true���򷵻�false
	 */
	@WebMethod
	public boolean clearlist(String userName) {
		for (User user : userList) {
			if (user.getName().equals(userName)) {
				user.getBacklogList().clear();
				return true;
			}
		}
		return false;
	}

	/**
	 * ���startDate��Ӧ��ʱ���Ƿ�����endDate��Ӧ��ʱ��
	 * 
	 * @param startDate
	 * @param endDate
	 * @return ���startDate��Ӧ��ʱ������endDate��Ӧ��ʱ�䷵��true����Ϊfalse
	 */
	static public boolean isRightDateOrder(Date startDate, Date endDate) {
		if (startDate.before(endDate)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��backlogs�а���ʼʱ��˳��������򣬲������е�Backlogת��ΪString���
	 * 
	 * @param backlogs
	 * @return
	 */
	static public String sortlist(List<DoList> backlogs) {
		String result = "";
		Collections.sort(backlogs);
		for (DoList backlog : backlogs) {
			result += backlog.toString() + "\r\n";
		}
		return result;
	}
	
 
}
