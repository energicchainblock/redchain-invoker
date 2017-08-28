package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
/**
 * 区块连基类请求
 * @author hunterfox
 * @date: 2017年8月28日
 * @version 1.0.0
 */
public abstract class TkcBaseRequest  implements Serializable {

	private static final long serialVersionUID = -7478761734955227237L;
	
	protected String publicKey;
	
    public TkcBaseRequest(String publicKey) {
         this.publicKey = publicKey;  
    }
	
	
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}	
}
