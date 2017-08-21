package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
/**
 * 用户证书相关信息
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
public class CaUserInfoDto implements Serializable{

	private static final long serialVersionUID = -1294664761824972857L;
	
	private String token;
	private String privateKey;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
