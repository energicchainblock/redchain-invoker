package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
/**
 * @author hunterfox
 * @date: 2017年7月31日
 * @version  1.0.0
 */
public abstract class ReqtBaseOrderDto implements Serializable {
	private static final long serialVersionUID = 1969579358303016443L;
	
	protected String cmd;
	protected String toAccount;
	protected String json;

	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd.toLowerCase();
	}
	
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getToAccount() {
		return toAccount;
	}
	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}
}
