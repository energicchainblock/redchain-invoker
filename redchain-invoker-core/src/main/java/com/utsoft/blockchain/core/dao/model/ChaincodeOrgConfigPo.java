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
 * 区块链组织配置
 * @author hunterfox
 * @date: 2017年7月29日
 * @version 1.0.0
 */
@Table(name = "t_chaincode_org_config")
public class ChaincodeOrgConfigPo implements Serializable {

	private static final long serialVersionUID = -6904149997676221663L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "chain_org_id")
	private int chainorgId;
	
	@Column(name = "mspid",nullable=false)
	private String mspId;
	
	@Column(name = "domain_name")
	private String domainName;
	
	/**
	 * 组织名称
	 */
	@Column(name = "orgname")
	private String orgname;
	
	
	@Column(name = "ca_locations")
	private String caLocations;
	
	public String getPeerLocations() {
		return peerLocations;
	}

	public void setPeerLocations(String peerLocations) {
		this.peerLocations = peerLocations;
	}

	@Column(name = "peer_locations")
	private String peerLocations;
	
	@Column(name = "order_locations")
	private String ordererLocations;
	
	@Column(name = "eventhub_locations")
	private String eventhubLocations;
	
	@Column(name = "channel_name")
	private String channelName;
	
	/**
	 * 状态
	 */
	private int status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "gmt_create")
    private Date gmtCreate;
	
	public String getMspId() {
		return mspId;
	}

	public void setMspId(String mspId) {
		this.mspId = mspId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getCaLocations() {
		return caLocations;
	}

	public void setCaLocations(String caLocations) {
		this.caLocations = caLocations;
	}

	public String getOrdererLocations() {
		return ordererLocations;
	}

	public void setOrdererLocations(String ordererLocations) {
		this.ordererLocations = ordererLocations;
	}

	public String getEventhubLocations() {
		return eventhubLocations;
	}

	public void setEventhubLocations(String eventhubLocations) {
		this.eventhubLocations = eventhubLocations;
	}	
	
	public Date getGmtCreate() {
		return gmtCreate;
	}
	
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getOrgname() {
		return orgname;
	}
	
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
