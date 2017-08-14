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
public class TransactionResultPo  implements Serializable {
	
	private static final long serialVersionUID = 7877754150521126742L;

	 @Id
	 @Column(name = "submitId",unique=true,length=64,nullable=false)
	 private String submitId;
	 
	 @Column(name = "txId",unique=true,length=64,nullable=false)
	 private String txId;
	 
	 @Column(name = "apply_code",unique=true,length=64,nullable=false)
	 private String applyCode;
	 
	 @Column(name = "result_status")
	 private byte resultStatus;
	 /**
	  * 用户账号
	  */
	 private String to;
	 
	 /**
	  * 0 回调结果成功
	  */
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
}
