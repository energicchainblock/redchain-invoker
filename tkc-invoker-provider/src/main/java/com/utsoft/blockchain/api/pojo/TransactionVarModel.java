package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.utsoft.blockchain.api.util.TransactionCmd;
/**
 * 交易业务分类和指令
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
public class TransactionVarModel implements Serializable {

	private static final long serialVersionUID = -1679807399924446074L;
	/**
	 *业务分类
	 */
	private String applyCategory;
	/**
	 * 指令
	 */
	private TransactionCmd cmd = TransactionCmd.MOVE ;
	/**
	 * from 转出账户
	 */
	private String from;
	/**
	 * to 转入账户
	 */
	private String to;
	
	/**
	 * 提交内容
	 */
	private String submitJson;
	/**
	 * created 10位提交时间戳
	 */
	private String created;
	
	/**
	 * 发起方携带私有数据，交完完成后，原封不懂得返回
	 */
	private Map<String,Object> externals = new HashMap<>();
	
	
	public TransactionVarModel(String applyCategory) {
		this.applyCategory = applyCategory; 
	}
	
	/**
	 * 交易义务代码分类和业务代码
	 * @param applyCategory
	 * @param cmd
	 */
	public TransactionVarModel(String applyCategory,TransactionCmd cmd) {
		this.applyCategory = applyCategory; 
		this.cmd = cmd;
	}
	public String getApplyCategory() {
		return applyCategory;
	}
	public void setApplyCategory(String applyCategory) {
		this.applyCategory = applyCategory;
	}
	public TransactionCmd getCmd() {
		return cmd;
	}
	public void setCmd(TransactionCmd cmd) {
		this.cmd = cmd;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubmitJson() {
		return submitJson;
	}
	public void setSubmitJson(String submitJson) {
		this.submitJson = submitJson;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public Map<String, Object> getExternals() {
		return externals;
	}
	public void setExternals(Map<String, Object> externals) {
		this.externals = externals;
	}
}
