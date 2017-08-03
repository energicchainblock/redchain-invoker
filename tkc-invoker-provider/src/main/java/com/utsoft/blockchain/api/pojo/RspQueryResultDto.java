package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
/**
 * @author hunterfox
 * @date: 2017年7月28日
 * @version  1.0.0
 */
public class RspQueryResultDto implements Serializable{

	private static final long serialVersionUID = 5211320007966306443L;
	
	private long timestamp;
	private String payload;
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
}
