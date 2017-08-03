package com.utsoft.blockchain.core.dao.model;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * 链码基本信息
 * @author hunterfox
 * @date: 2017年7月28日
 * @version 1.0.0
 */
@Table(name = "t_chaincode")
public class ChaincodePo implements Serializable {

	private static final long serialVersionUID = -1713147924020401334L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY,generator="Mysql") 
	@Column(name = "chainId")
	private  Integer chainId;
	
	@Column(name = "chain_code_name")
	private String chainCodeName;
	
	@Column(name = "code_version")
	private String codeVersion;
	
	@Column(name = "code_path")
	private String codePath;
	
	
	
	private int status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "gmt_create")
    private Date gmtCreate;

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

	public Integer getChainId() {
		return chainId;
	}

	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	public String getChainCodeName() {
		return chainCodeName;
	}
	public void setChainCodeName(String chainCodeName) {
		this.chainCodeName = chainCodeName;
	}

	public String getCodeVersion() {
		return codeVersion;
	}

	public void setCodeVersion(String codeVersion) {
		this.codeVersion = codeVersion;
	}

	public String getCodePath() {
		return codePath;
	}

	public void setCodePath(String codePath) {
		this.codePath = codePath;
	}
	
}
