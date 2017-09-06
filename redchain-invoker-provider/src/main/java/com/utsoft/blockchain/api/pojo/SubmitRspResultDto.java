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
	/**
	 * -1 标识未确定 
	 * 0 失败
	 * 1 标识成功
	 */
	private  int status = -1;
	public String getTxId() {
		return txId;
	}
	public void setTxId(String txId) {
		this.txId = txId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
