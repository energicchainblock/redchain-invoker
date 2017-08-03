package com.utsoft.blockchain.core.dao.model;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 关键信息存储
 * @author hunterfox
 * @date: 2017年7月29日
 * @version  1.0.0
 */
@Table(name = "t_keystore")
public class KeyStorePo  implements Serializable {

	private static final long serialVersionUID = 6175978575785650366L;
	@Id
	@Column(name = "keyId")
	private String keyId;
	
	private String store;
	
	public String getKeyId() {
		return keyId;
	}
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
}
