package com.utsoft.blockchain.core.pojo;
import java.io.Serializable;
/**
 * 回调通知
 * @author hunterfox
 * @date: 2017年8月15日
 * @version 1.0.0
 */
public class TransactionCallRsp implements Serializable {

	private static final long serialVersionUID = 284967083619518853L;
    
	private int status;
	private String msg;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
