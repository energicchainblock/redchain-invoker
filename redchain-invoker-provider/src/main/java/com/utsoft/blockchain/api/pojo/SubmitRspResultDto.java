package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
/**
 * @author hunterfox
 * @date: 2017年7月31日
 * @version  1.0.0
 */
public class SubmitRspResultDto implements Serializable  {

	private static final long serialVersionUID = 1L;
    
	private String txId;
	private  boolean status = false;
	public String getTxId() {
		return txId;
	}
	public void setTxId(String txId) {
		this.txId = txId;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
}
