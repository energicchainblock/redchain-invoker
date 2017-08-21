package com.utsoft.blockchain.api.pojo;
import java.io.Serializable;

import com.utsoft.blockchain.api.util.Constants;
/**
 * 统一返回处理
 * @author <a href="flyskyhunter@gmail.com">王波</a> 
 * @date  2017年7月17日
 * @version 1.0.0
 * @param <T>
 */
public class BaseResponseModel<T> implements Serializable {

    private static final long serialVersionUID = 3771700626958736013L;
    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    private BaseResponseModel() {
        code = Constants.OK;
    }

    private BaseResponseModel(String message) {
        this();
        this.message = message;
    }
    
    private BaseResponseModel(int code) {
        this();
        this.code = code;
    }

    private BaseResponseModel(int code, String message) {
        this.code = code;
        this.message = message;

    }

    private BaseResponseModel(T t) {
        this();
        data = t;
    }
    
    public boolean isSuccess() {
        return this.code == Constants.OK;
    }
    
    public static <T> BaseResponseModel<T> build() {
    	return new BaseResponseModel<>();
    }
    
    public static <T> BaseResponseModel<T> build(int code) {
    	return new BaseResponseModel<>(code);
    }
    
    public static <T> BaseResponseModel<T> build(int code, String message) {
    	return new BaseResponseModel<>(code, message);
    }
    
    public static <T> BaseResponseModel<T> build(T data) {
    	return new BaseResponseModel<>(data);
    }
    
    public static <T> BaseResponseModel<T> build(String message) {
    	return new BaseResponseModel<>(message);
    }

	public int getCode() {
		return code;
	}

	public BaseResponseModel<T> setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public BaseResponseModel<T> setMessage(String message) {
		this.message = message;
		return this;
	}

	public T getData() {
		return data;
	}

	public BaseResponseModel<T> setData(T data) {
		this.data = data;
		return this;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}