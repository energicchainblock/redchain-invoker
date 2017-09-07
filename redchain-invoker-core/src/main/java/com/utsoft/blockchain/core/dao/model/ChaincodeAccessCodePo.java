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
 * 访问业务代码基本信息
 * @author hunterfox
 * @date 2017年7月17日
 * @version 1.0.0
 */
@Table(name = "t_chaincode_caccess_code")
public class ChaincodeAccessCodePo implements Serializable {

	private static final long serialVersionUID = 13232434286L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY,generator="Mysql") 
	@Column(name = "applyId")
	private  Integer applyId;
	/**
	 * 对外标识
	 */
	@Column(name = "apply_code")
	private  String applyCode;
	
	/**
	 * 访问某款链码
	 */
	@Column(name = "chainId")
	private Integer chainId;
	
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 评论
	 */
	private  String remark;
	
	@Column(name = "url_address")
	private String urlAddress;
	
	
	@Column(name = "worldstate_url")
	private String worldstateUrl;
	
	
	public String getWorldstateUrl() {
		return worldstateUrl;
	}

	public void setWorldstateUrl(String worldstateUrl) {
		this.worldstateUrl = worldstateUrl;
	}
	
	/**
	 * 支持http 方法
	 */
	 @Column(name = "support_method",nullable=false)
	 private String supportMethod;
	 
	  public String getSupportMethod() {
		 return supportMethod;
	  }

	  public void setSupportMethod(String supportMethod) {
	    this.supportMethod = supportMethod;
	   }
	
	/**
	 * 时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "gmt_create")
    private Date gmtCreate;

	public Integer getApplyId() {
		return applyId;
	}

	public void setApplyId(Integer applyId) {
		this.applyId = applyId;
	}

	public String getApplyCode() {
		return applyCode;
	}

	public void setApplyCode(String applyCode) {
		this.applyCode = applyCode;
	}

	public Integer getChainId() {
		return chainId;
	}

	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
	public String getUrlAddress() {
		return urlAddress;
	}

	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}
}
