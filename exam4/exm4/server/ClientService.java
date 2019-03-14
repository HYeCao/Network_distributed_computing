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
 * 用户操作类
 * @author dell
 *
 */
@WebService(name="clientservice",  portName="clientserviceport", targetNamespace="http://www.exam4server.com")
public class ClientService {

	/**
	 * 定义用户列表
	 */
	private List<User> userList;
	/**
	 * 统一时间格式
	 */
	private SimpleDateFormat simpleDateFormat;
	/**
	 * 时间格式
	 */
	static final String dateFormat = "yyyy-MM-dd HH:mm";
	/**
	 * 构造函数定义
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
	 * 主函数的启动，开启服务
	 * @param args
	 */
	public static void main(String[] args) {  
        Endpoint.publish("http://127.0.0.1:8002/webservice/clientservice", new ClientService());  
        //wsimport -keep http://127.0.0.1:8002/webservice/clientservice?wsdl
        System.out.println("已启动服务");
    }
	/**
	 * 注册用户信息
	 * @param userName 需要注册的用户名，需要与之前的用户名不重复
	 * @param password 需要注册的用户密码
	 * @return 如果重复返回exist 否则返回ok
	 * 新用户可以注册。新用户必须提供用户名和密码。
	 * 如果提供的用户名称已经存在，打印一个错误信息。
	 * 注册成功后，打印一条消息说明注册成功。
	 * 在新用户注册时，需要为这个新用户创建一个新的待见事项列表对象。
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
	 * 添加待办事项函数
	 * @param userName 需要添加待办事项的用户名
	 * @param startDateStr 开始时间 
	 * @param endDateStr 结束时间 
	 * @param title 待办事项标题
	 * @return 如果添加成功返回true否则返回false
	 * @throws ParseException 如果startDateStr和endDateStr格式不对抛出错误
	 */
	@WebMethod
	public boolean addlist(String userName, String startDateStr, String endDateStr, String title) throws ParseException {
		/**
		 * 开始时间以及结束时间的规范化
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
	 * 查询待办事项
	 * @param userName     需要进行查询的用户名
	 * @param startDateStr 开始时间 
	 * @param endDateStr   结束时间 
	 * @return             如果用户名存在返回查询事项信息，否则返回fail
	 * @throws ParseException 如果startDateStr和endDateStr格式不对抛出错误
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
	 * 检验用户名密码是否正确函数
	 * @param userName 用户名
	 * @param password 密码
	 * @return 如果正确返回true否则返回false
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
	 * 注册用户可以从他们的待办事项列表中删除项目。参数是将被删除的项目的ID
	 * @param userName 用户名
	 * @param backlogId 需要删除待办事项的id
	 * @return 删除成功返回true否则返回false
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
	 * 注册用户可以清空待办事项列表中的所有项目
	 * @param userName 需要清除待办事项的用户名
	 * @return 如清除成功返回true否则返回false
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
	 * 检测startDate对应的时间是否早于endDate对应的时间
	 * 
	 * @param startDate
	 * @param endDate
	 * @return 如果startDate对应的时间早于endDate对应的时间返回true否则为false
	 */
	static public boolean isRightDateOrder(Date startDate, Date endDate) {
		if (startDate.before(endDate)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 对backlogs中按开始时间顺序进行排序，并将所有的Backlog转化为String输出
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
