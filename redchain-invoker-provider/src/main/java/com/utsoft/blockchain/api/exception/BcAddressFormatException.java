package com.utsoft.blockchain.api.exception;
/**
 * 
 * @author hunterfox
 * @date: 2017年8月25日
 * @version 1.0.0
 */
public class BcAddressFormatException extends BasisException {

	private static final long serialVersionUID = 4685991922697364831L;
	
    public BcAddressFormatException(String message) {
        super(message);
    }

    public BcAddressFormatException(Throwable t) {
        super(t);
    }
    
	public BcAddressFormatException(String message, Throwable parent) {
		super(message, parent);
	}
}
