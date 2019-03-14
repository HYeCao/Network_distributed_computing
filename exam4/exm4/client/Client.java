package exm4.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.exam4server.ClientServiceService;
import com.exam4server.Clientservice;

/**
 * 用户客户端类
 */
public class Client {

	/**
	 * 时间格式统一
	 */
	static final String dateFormat = "yyyy-MM-dd HH:mm";

	static final String inputDateFormat = "年-月-日 小时：分钟";

	public Client() {
	}

	public static void main(String[] args) {
		String order1 = "注册菜单：\r\n" + "    1、注册\r\n" + "    0、退出\r\n";
		String order2 = "功能菜单：\r\n" + "     1、添加项目\r\n" + "     2、查询项目\r\n" + "     3、删除项目\r\n" + "     4、清空项目\r\n"
				+ "     0、返回上一菜单\r\n";
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			/*
			 * 首先定义基本输入判别数据，以及操作数
			 */
			String userName, password, repassword;
			int input;
			/**
			 * 服务执行类的定义
			 */
			ClientServiceService clientOperationService = new ClientServiceService();
			Clientservice clientoperation = clientOperationService.getClientserviceport();
			while (true) {
				/**
				 * 注册功能的执行
				 */
				System.out.println(order1);
				System.out.println("请输入所需操作");
				input = Integer.parseInt(bufferedReader.readLine());

				if (input == 1) {
					System.out.println("请输出用户名：");
					userName = bufferedReader.readLine();
					System.out.println("请输入密码：");
					password = bufferedReader.readLine();
					Boolean temp = clientoperation.register(userName, password);
					if (temp != true) {
						System.out.println("注册失败，用户名已经存在");
					} else {

						System.out.println("登录成功");
						/**
						 * 登录成功后执行具体的操作
						 */
						while (true) {
							System.out.println(order2);
							System.out.println("请输入所需操作");
							input = Integer.parseInt(bufferedReader.readLine());
							String title, startDateStr, endDateStr, backlogId, ensure;
							/**
							 * 通过不同输入的操作数的判别来执行不同的功能
							 */
							if (input == 0) {
								break;
							} else {
								/**
								 * 通过switch case实现不同功能的执行
								 */
								switch (input) {
								case 1:
									/**
									 * 添加项目执行
									 */
									System.out.println("请输入要添加项目的名称:");
									title = bufferedReader.readLine();
									System.out.println("请输入项目起始时间：");
									System.out.println(inputDateFormat);
									startDateStr = bufferedReader.readLine();
									System.out.println("请输入项目截止时间：");
									System.out.println(inputDateFormat);
									endDateStr = bufferedReader.readLine();
									if (isRightDateFormat(endDateStr) && isRightDateFormat(startDateStr)) {
										if (isRightDateOrder(startDateStr, endDateStr)) {
											if (clientoperation.addlist(userName, startDateStr, endDateStr, title)) {
												System.out.println("添加成功");
											} else {
												System.out.println("添加失败");
											}
										} else {
											System.out.println("时间顺序输入错误");
										}
									} else {
										System.out.println("时间格式输入错误");
									}
									break;
								case 2:
									/**
									 * 查询项目执行
									 */
									System.out.println("请输入起始时间：");
									System.out.println(inputDateFormat);
									startDateStr = bufferedReader.readLine();
									System.out.println("请输入截止时间：");
									System.out.println(inputDateFormat);
									endDateStr = bufferedReader.readLine();
									if (isRightDateFormat(endDateStr) && isRightDateFormat(startDateStr)) {
										if (isRightDateOrder(startDateStr, endDateStr)) {
											String result = clientoperation.querylist(userName, startDateStr,
													endDateStr);
											if (result.length() < 7 && result.equals("fail")) {
												System.out.println("查询失败");
											} else {
												System.out.println("查询成功，结果如下");
												System.out.println(result);
											}
										} else {
											System.out.println("时间顺序输入错误");
										}
									} else {
										System.out.println("时间格式输入错误");
									}

									break;
								case 3:
									/**
									 * 通过项目ID判断 实现删除操作
									 */
									System.out.println("请输入所要删除项目的id:");
									backlogId = bufferedReader.readLine();
									if (clientoperation.deletelist(userName, backlogId)) {
										System.out.println("删除成功");
									} else {
										System.out.println("删除失败，该id不存在");
									}
									break;
								case 4:
									/**
									 * 清楚项目执行
									 */
									System.out.println("清除请输入1\r\n输入其他返回");
									ensure = bufferedReader.readLine();
									if (ensure.equals("1")) {
										if (clientoperation.clearlist(userName)) {
											System.out.println("清除成功");
										} else {
											System.out.println("清除失败");
										}
									}
									break;

								default:
									System.out.println("错误操作");
									break;
								}
							}
						}
					}
				} else if (input == 0) {
					break;
				} else {
					System.out.println("错误操作");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 是否是可格式化的dateStr
	 * 
	 * @param dateStr
	 * @return 如果dateStr可以被转换为dateFormate形式的date返回true，否则返回false
	 */
	static public boolean isRightDateFormat(String dateStr) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		try {
			simpleDateFormat.parse(dateStr);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * startDateStr对应的时间是否早于endDateStr对应的时间
	 * 
	 * @param startDateStr
	 * @param endDateStr
	 * @return boolean
	 */
	static public boolean isRightDateOrder(String startDateStr, String endDateStr) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		try {
			Date startDate = simpleDateFormat.parse(startDateStr);
			Date endDate = simpleDateFormat.parse(endDateStr);
			if (startDate.before(endDate)) {
				return true;
			} else {

				return false;
			}
		} catch (ParseException e) {
			return false;
		}
	}
}
