package com.utsoft.blockchain.core.dao.model;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * 交易结果
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Entity
@Table(name = "t_chain_transaction")
public class TransactionResultPo implements Serializable {
	
	private static final long serialVersionUID = 7877754150521126742L;

	 @Id
	 @Column(name = "txId",unique=true,length=64,nullable=false)
	 private String txId;
	 
	 /**
	  * 用户账号
	  */
	 @Column(name = "toAccount")
	 private String to;
	 
	 @Column(name = "submitId")
	 private String submitId;
	 
	 @Column(name = "apply_code")
	 private String applyCode;
	 
	 @Column(name = "result_status")
	 private byte resultStatus;
	 
	 @Column(name = "block_status")
	 private byte blockStatus;

	 /**
	  * 回调次数
	  */
	 @Column(name = "counter")
	 private byte counter;
	  
	 /**
	  * 2:没有回调地址
	  * -1 失败
	  * 200  回调结果成功
	  * 0  没有开启
	  */
	 @Column(name = "tstatus")
	 private int status;
	 
	 @Temporal(TemporalType.TIMESTAMP)
 	 @Column(name = "gmt_create")
     private Date gmtCreate;
	 
	 
	 @Temporal(TemporalType.TIMESTAMP)
 	 @Column(name = "callback_time")
     private Date callbackTime;
 
	public String getApplyCode() {
		return applyCode;
	}

	public void setApplyCode(String applyCode) {
		this.applyCode = applyCode;
	}

	public byte getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(byte resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getCallbackTime() {
		return callbackTime;
	}

	public void setCallbackTime(Date callbackTime) {
		this.callbackTime = callbackTime;
	}

	public String getSubmitId() {
		return submitId;
	}

	public void setSubmitId(String submitId) {
		this.submitId = submitId;
	}

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
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public byte getCounter() {
		return counter;
	}
	public void setCounter(byte counter) {
		this.counter = counter;
	}
	public byte getBlockStatus() {
		return blockStatus;
	}

	public void setBlockStatus(byte blockStatus) {
		this.blockStatus = blockStatus;
	}
}
