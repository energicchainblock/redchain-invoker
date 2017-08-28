package com.utsoft.blockchain.api.exception;
/**
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class BasisException extends Exception {
	
	private static final long serialVersionUID = -1786006062939246284L;

    public BasisException(String message) {
        super(message);
    }

    public BasisException(Throwable t) {
        super(t);
    }
    
    public BasisException(String message, Throwable parent) {
        super(message, parent);
    }
}
