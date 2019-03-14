package exm4.server.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {


	String name;
	String password;
	List<DoList> DoList;
	

	public User() {
		// TODO Auto-generated constructor stub
	}


	public User(String name, String password) {
		super();
		this.name = name;
		this.password = password;
		this.DoList=new ArrayList<DoList>();
	}

    private static final long serialVersionUID = 7569342507666930139L;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * @return the backlogList
	 */
	public List<DoList> getBacklogList() {
		return DoList;
	}


	/**
	 * @param backlogList the backlogList to set
	 */
	public void setBacklogList(List<DoList> backlogList) {
		this.DoList = backlogList;
	}
	
	
	
}
