package com.utsoft.blockchain.core.util;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
/**
 * @author hunterfox
 * @date: 2017年8月5日
 * @version 1.0.0
 */
public class InstantiationException extends ServiceProcessException {

	private static final long serialVersionUID = -7906120123110847469L;
	public InstantiationException(int errorCode, String message) {
		super(errorCode, message);
	}
}
