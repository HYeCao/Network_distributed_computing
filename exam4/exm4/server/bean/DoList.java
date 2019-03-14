package exm4.server.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;



/**
 * ����������
 */
public class DoList implements Serializable,Comparable<DoList> {


	/**
	 * ʱ���ʽ
	 */
	static final String dateFormat = "yyyy-MM-dd HH:mm";
	/**
	 * ��������Ψһidʹ��UUID����
	 */
	String listID;
	Date startDate;
	Date endDate;
	String title;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3001061713873657187L;
	public DoList() {
		// TODO Auto-generated constructor stub
	}

	public DoList(String backlogId, Date startDate, Date endDate, String title) {
		super();
		this.listID = backlogId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.title = title;
	}
	public DoList(Date startDate, Date endDate, String title) {
		super();
		this.listID = UUID.randomUUID().toString();
		this.startDate = startDate;
		this.endDate = endDate;
		this.title = title;
	}

	/**
	 * @return the backlogId
	 */
	public String getBacklogId() {
		return listID;
	}

	/**
	 * @param backlogId the backlogId to set
	 */
	public void setBacklogId(String backlogId) {
		this.listID = backlogId;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * ����Ƿ񲻰���ָ��������
	 * 
	 * @param startDate
	 *            ָ����ʼʱ��
	 * @param endDate
	 *            ָ������ʱ��
	 * @return ֻҪĿ�����������ڱ�������������֮�ⷵ��true������Ϊfalse
	 */
	public boolean noContainDates(Date startDate, Date endDate) {
		if (startDate.before(endDate)) {
			if (endDate.before(this.startDate) || startDate.after(this.endDate)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		return "��ĿidΪ��" + listID + " | ��Ŀ����Ϊ��" + title  + " | ��ʼʱ��Ϊ��" + simpleDateFormat.format(startDate) + " | ����ʱ��Ϊ��"
				+ simpleDateFormat.format(endDate);
	}

	@Override
	public int compareTo(DoList o) {
		if(this.startDate.before(o.startDate))
			return-1;
		else {
			return 1;
		}
	}
	
}
