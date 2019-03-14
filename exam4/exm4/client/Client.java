package exm4.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.exam4server.ClientServiceService;
import com.exam4server.Clientservice;

/**
 * �û��ͻ�����
 */
public class Client {

	/**
	 * ʱ���ʽͳһ
	 */
	static final String dateFormat = "yyyy-MM-dd HH:mm";

	static final String inputDateFormat = "��-��-�� Сʱ������";

	public Client() {
	}

	public static void main(String[] args) {
		String order1 = "ע��˵���\r\n" + "    1��ע��\r\n" + "    0���˳�\r\n";
		String order2 = "���ܲ˵���\r\n" + "     1�������Ŀ\r\n" + "     2����ѯ��Ŀ\r\n" + "     3��ɾ����Ŀ\r\n" + "     4�������Ŀ\r\n"
				+ "     0��������һ�˵�\r\n";
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			/*
			 * ���ȶ�����������б����ݣ��Լ�������
			 */
			String userName, password, repassword;
			int input;
			/**
			 * ����ִ����Ķ���
			 */
			ClientServiceService clientOperationService = new ClientServiceService();
			Clientservice clientoperation = clientOperationService.getClientserviceport();
			while (true) {
				/**
				 * ע�Ṧ�ܵ�ִ��
				 */
				System.out.println(order1);
				System.out.println("�������������");
				input = Integer.parseInt(bufferedReader.readLine());

				if (input == 1) {
					System.out.println("������û�����");
					userName = bufferedReader.readLine();
					System.out.println("���������룺");
					password = bufferedReader.readLine();
					Boolean temp = clientoperation.register(userName, password);
					if (temp != true) {
						System.out.println("ע��ʧ�ܣ��û����Ѿ�����");
					} else {

						System.out.println("��¼�ɹ�");
						/**
						 * ��¼�ɹ���ִ�о���Ĳ���
						 */
						while (true) {
							System.out.println(order2);
							System.out.println("�������������");
							input = Integer.parseInt(bufferedReader.readLine());
							String title, startDateStr, endDateStr, backlogId, ensure;
							/**
							 * ͨ����ͬ����Ĳ��������б���ִ�в�ͬ�Ĺ���
							 */
							if (input == 0) {
								break;
							} else {
								/**
								 * ͨ��switch caseʵ�ֲ�ͬ���ܵ�ִ��
								 */
								switch (input) {
								case 1:
									/**
									 * �����Ŀִ��
									 */
									System.out.println("������Ҫ�����Ŀ������:");
									title = bufferedReader.readLine();
									System.out.println("��������Ŀ��ʼʱ�䣺");
									System.out.println(inputDateFormat);
									startDateStr = bufferedReader.readLine();
									System.out.println("��������Ŀ��ֹʱ�䣺");
									System.out.println(inputDateFormat);
									endDateStr = bufferedReader.readLine();
									if (isRightDateFormat(endDateStr) && isRightDateFormat(startDateStr)) {
										if (isRightDateOrder(startDateStr, endDateStr)) {
											if (clientoperation.addlist(userName, startDateStr, endDateStr, title)) {
												System.out.println("��ӳɹ�");
											} else {
												System.out.println("���ʧ��");
											}
										} else {
											System.out.println("ʱ��˳���������");
										}
									} else {
										System.out.println("ʱ���ʽ�������");
									}
									break;
								case 2:
									/**
									 * ��ѯ��Ŀִ��
									 */
									System.out.println("��������ʼʱ�䣺");
									System.out.println(inputDateFormat);
									startDateStr = bufferedReader.readLine();
									System.out.println("�������ֹʱ�䣺");
									System.out.println(inputDateFormat);
									endDateStr = bufferedReader.readLine();
									if (isRightDateFormat(endDateStr) && isRightDateFormat(startDateStr)) {
										if (isRightDateOrder(startDateStr, endDateStr)) {
											String result = clientoperation.querylist(userName, startDateStr,
													endDateStr);
											if (result.length() < 7 && result.equals("fail")) {
												System.out.println("��ѯʧ��");
											} else {
												System.out.println("��ѯ�ɹ����������");
												System.out.println(result);
											}
										} else {
											System.out.println("ʱ��˳���������");
										}
									} else {
										System.out.println("ʱ���ʽ�������");
									}

									break;
								case 3:
									/**
									 * ͨ����ĿID�ж� ʵ��ɾ������
									 */
									System.out.println("��������Ҫɾ����Ŀ��id:");
									backlogId = bufferedReader.readLine();
									if (clientoperation.deletelist(userName, backlogId)) {
										System.out.println("ɾ���ɹ�");
									} else {
										System.out.println("ɾ��ʧ�ܣ���id������");
									}
									break;
								case 4:
									/**
									 * �����Ŀִ��
									 */
									System.out.println("���������1\r\n������������");
									ensure = bufferedReader.readLine();
									if (ensure.equals("1")) {
										if (clientoperation.clearlist(userName)) {
											System.out.println("����ɹ�");
										} else {
											System.out.println("���ʧ��");
										}
									}
									break;

								default:
									System.out.println("�������");
									break;
								}
							}
						}
					}
				} else if (input == 0) {
					break;
				} else {
					System.out.println("�������");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �Ƿ��ǿɸ�ʽ����dateStr
	 * 
	 * @param dateStr
	 * @return ���dateStr���Ա�ת��ΪdateFormate��ʽ��date����true�����򷵻�false
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
	 * startDateStr��Ӧ��ʱ���Ƿ�����endDateStr��Ӧ��ʱ��
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
