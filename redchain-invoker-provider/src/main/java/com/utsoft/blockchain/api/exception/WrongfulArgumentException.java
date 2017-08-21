package com.utsoft.blockchain.api.exception;
/**
 * 
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class WrongfulArgumentException extends BasisException {
    private static final long serialVersionUID = -6094512275378074427L;

    public WrongfulArgumentException(String message, Exception parent) {
        super(message, parent);
    }

    public WrongfulArgumentException(String message) {
        super(message);
    }

    public WrongfulArgumentException(Throwable t) {
        super(t);
    }
}
