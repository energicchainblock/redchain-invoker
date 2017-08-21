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
 * fabric users
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
@Table(name = "t_fabric_ca_user")
public class FabricCaUserPo implements Serializable {

	private static final long serialVersionUID = 4296765329666816264L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY,generator="Mysql") 
	@Column(name = "userId")
	private String userId;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "cert")
	private String cert;
	
	@Column(name = "account")
	private String account;
	
	@Column(name = "affiliation")
	private String affiliation;
	
	@Column(name = "organization")
    private String organization;
    
    @Column(name = "enrollment_secret")
    private String enrollmentSecret;
    
    @Column(name = "mspId")
    private  String mspId;
    
	private int status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "gmt_create")
	private Date gmtCreate;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getEnrollmentSecret() {
		return enrollmentSecret;
	}

	public void setEnrollmentSecret(String enrollmentSecret) {
		this.enrollmentSecret = enrollmentSecret;
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
	
	public String getMspId() {
		return mspId;
	}

	public void setMspId(String mspId) {
	   this.mspId = mspId;
	}
}
