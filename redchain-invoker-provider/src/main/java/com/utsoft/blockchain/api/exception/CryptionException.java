package com.utsoft.blockchain.api.exception;
/**
 * 
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class CryptionException extends BasisException {

	private static final long serialVersionUID = 2535628823414100057L;

	public CryptionException(String message, Exception parent) {
		super(message, parent);
	}

	public CryptionException(String message) {
		super(message);
	}
}
