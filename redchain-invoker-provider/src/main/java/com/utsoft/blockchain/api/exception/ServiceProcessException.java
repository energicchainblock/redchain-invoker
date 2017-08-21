package com.utsoft.blockchain.api.exception;

import com.utsoft.blockchain.api.util.Constants;

/**
 * 异常
 * @author <a href="flyskyhunter@gmail.com">王波</a> 
 * @date  2017年7月6日
 * @version 1.0.0
 */
public class ServiceProcessException extends RuntimeException {

	private static final long serialVersionUID = 7188131156074752217L;
	private int errorCode;
	
	/**
	 * 默认为执行失败
	 * @param message
	 */
	public ServiceProcessException(String message) {
		super(message);
		this.errorCode = Constants.EXECUTE_FAIL_ERROR; 
	}
	
	public ServiceProcessException(int errorCode,String message) {
		super(message);
		this.errorCode = errorCode; 
	}

	public static ServiceProcessException build(String msg,int errorCode) {
		return  new ServiceProcessException(errorCode,msg);
	}
	
    public ServiceProcessException(int errorCode,String message,Throwable cause) {
	      super(message, cause);
	      this.errorCode = errorCode; 
    }
    
    
    /** 返回错误代码。
    *
    * @return 错误代码的。
    */
    public int getErrorCode() {
       return errorCode;
    }
    
    @Override
    public String toString() {
        return "[error code]:" + errorCode + ", "
                + "[message]:" + getMessage();
    }
}
