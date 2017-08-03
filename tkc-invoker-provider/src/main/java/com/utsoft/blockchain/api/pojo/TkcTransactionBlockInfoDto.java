package com.utsoft.blockchain.api.pojo;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * 区块链事务查询信息
 * @author hunterfox
 * @date: 2017年7月31日
 * @version  1.0.0
 */
public class TkcTransactionBlockInfoDto  implements Serializable{

	private static final long serialVersionUID = 1921862478625022644L;
	
	/**
	 * 区块链上 block number
	 */
	private Long blockNumber;
	/**
	 * 区块链先前一个block hash
	 */
	private String previousHash;
	
	/**
	 * 当前链block hash 
	 */
	private String chainCurrentHash;
	
	private String datahash;
	
	private Integer txValCodeNumber;
	
	public JSONObject getCommits() {
		return commits;
	}
	public void setCommits(JSONObject commits) {
		this.commits = commits;
	}
	private JSONObject commits = new  JSONObject();
	
	public String getChainCurrentHash() {
		return chainCurrentHash;
	}
	public void setChainCurrentHash(String chainCurrentHash) {
		this.chainCurrentHash = chainCurrentHash;
	}
	
	public Long getBlockNumber() {
		return blockNumber;
	}
	public void setBlockNumber(Long blockNumber) {
		this.blockNumber = blockNumber;
	}
	public String getPreviousHash() {
		return previousHash;
	}
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}
	public Integer getTxValCodeNumber() {
		return txValCodeNumber;
	}
	public void setTxValCodeNumber(Integer txValCodeNumber) {
		this.txValCodeNumber = txValCodeNumber;
	}
	
	public String getDatahash() {
		return datahash;
	}
	public void setDatahash(String datahash) {
		this.datahash = datahash;
	}
}
