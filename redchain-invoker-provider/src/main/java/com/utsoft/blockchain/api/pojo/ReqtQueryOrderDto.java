package com.utsoft.blockchain.api.pojo;
/**
 * @author hunterfox
 * @date: 2017年7月28日
 * @version  1.0.0
 */
public class ReqtQueryOrderDto extends ReqtBaseOrderDto {

	private static final long serialVersionUID = -4927898623909069055L;

	@Override
	public String toString() {
		return "QueryOrderDto [cmd=" + cmd + ", toAccount=" + toAccount + ", json=" + json + "]";
	}
}
