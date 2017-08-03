package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * 提交
 * @author hunterfox
 * @date 2017年7月18日
 * @version 1.0.0
 */
public class TkcSubmitRspVo  extends SubmitRspResultDto implements Serializable {
	private static final long serialVersionUID = -6217395086060284504L;

	/**
	 * 发起方携带私有数据，交完完成后，原封不懂得返回
	 */
	private Map<String,Object> externals = new HashMap<>();

	public Map<String, Object> getExternals() {
		return externals;
	}

	public void setExternals(Map<String, Object> externals) {
		this.externals = externals;
	}
}
