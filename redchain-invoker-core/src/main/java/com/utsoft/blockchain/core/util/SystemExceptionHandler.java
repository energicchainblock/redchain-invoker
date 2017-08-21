package com.utsoft.blockchain.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;
/**
 * exception handler 
 * @author hunterfox
 * @date: 2017年8月5日
 * @version 1.0.0
 */
public class SystemExceptionHandler {

	private static final Log logger = LogFactory.getLog(SystemExceptionHandler.class);
	private static SystemExceptionHandler instance = new SystemExceptionHandler();
	
	 private SystemExceptionHandler() {	
	 }
	
	public static SystemExceptionHandler getInstance(){
		return instance;
	}
	/**
	 * 异常处理
	 * @param failure
	 */
	public void handlerException(Throwable failure){
		
		if (logger.isErrorEnabled()) {
			logger.error("tkc start server startup failed", failure);
		}
		ReflectionUtils.rethrowRuntimeException(failure);
	}
}
