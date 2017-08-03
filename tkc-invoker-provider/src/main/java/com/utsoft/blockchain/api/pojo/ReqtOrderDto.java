package com.utsoft.blockchain.api.pojo;

/**
 * @author hunterfox
 * @date: 2017年7月28日
 * @version  1.0.0
 */
public class ReqtOrderDto extends ReqtBaseOrderDto {

	private static final long serialVersionUID = -3873451724505253164L;
	private String fromAccount;
	
	public String getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}
}
