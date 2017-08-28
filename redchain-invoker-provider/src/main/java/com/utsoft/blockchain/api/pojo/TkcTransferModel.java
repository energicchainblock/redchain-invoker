package com.utsoft.blockchain.api.pojo;

/**
 * 交易业务分类和指令
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
public class TkcTransferModel extends TransactionBaseModel {

	private static final long serialVersionUID = -1679807399924446074L;
	
	public TkcTransferModel(String publicKey,String applyCategory, String serviceCode) {
		super(publicKey,applyCategory, serviceCode);
	}
	/**
	 * from 转出账户
	 */
	private String from;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
