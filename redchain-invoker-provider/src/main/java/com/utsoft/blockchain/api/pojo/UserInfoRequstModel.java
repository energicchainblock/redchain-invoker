package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
/**
 * 用户请求信息
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
public class UserInfoRequstModel  implements Serializable {

	private static final long serialVersionUID = -5220563748176568920L;
	private String userName;
	private String password;
	private String 	created;
	/**
	 * 属于哪个组织
	 */
	//private String partOforg;
	
	/**
	 * 组织附属关系
	 */
	//private String affliation;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

//	public String getPartOforg() {
//		return partOforg;
//	}
//
//	public void setPartOforg(String partOforg) {
//		this.partOforg = partOforg;
//	}

	/*public String getAffliation() {
		return affliation;
	}

	public void setAffliation(String affliation) {
		this.affliation = affliation;
	}*/
}
