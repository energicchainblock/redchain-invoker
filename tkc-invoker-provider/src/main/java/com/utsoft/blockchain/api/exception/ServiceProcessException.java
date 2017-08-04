package com.utsoft.blockchain.api.exception;
/**
 * 服务器异常
 * @author <a href="flyskyhunter@gmail.com">王波</a> 
 * @date  2017年7月6日
 * @version 1.0.0
 */
public class ServiceProcessException extends RuntimeException {

	private static final long serialVersionUID = 7188131156074752217L;

	public ServiceProcessException(String message) {
		super(message);
	}

	public static ServiceProcessException build(String msg) {
		return  new ServiceProcessException(msg);
	}
	
   public ServiceProcessException(String message, Throwable cause) {
	        super(message, cause);
    }
}
