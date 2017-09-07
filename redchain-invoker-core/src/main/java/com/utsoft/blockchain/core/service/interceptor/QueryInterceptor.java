package com.utsoft.blockchain.core.service.interceptor;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
/**
 * 某项业务，某项拦截
 * @author hunterfox
 * @date: 2017年9月7日
 * @version 1.0.0
 */
public interface QueryInterceptor {

	
	 TkcQueryDetailRspVo interceptor(String ... request);
	
	 boolean isAppcodeMatch(String applycode,String cmd);
}
