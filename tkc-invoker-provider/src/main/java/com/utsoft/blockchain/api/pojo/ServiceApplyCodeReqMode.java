package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
/**
 * 业务申请注册对象
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
public class ServiceApplyCodeReqMode implements Serializable {

	private static final long serialVersionUID = -991191534689212648L;

	/**
	 * 业务申请号码
	 */
	private String applyCode;
	/**
	 * 地址
	 */
	private String callbackUrl;
	public String getApplyCode() {
		return applyCode;
	}
	public void setApplyCode(String applyCode) {
		this.applyCode = applyCode;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}	
}
