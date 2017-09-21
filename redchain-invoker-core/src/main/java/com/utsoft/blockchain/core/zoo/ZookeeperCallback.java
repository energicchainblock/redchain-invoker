package com.utsoft.blockchain.core.zoo;
/**
 * 
 * @author hunterfox
 * @date: 2017年9月21日
 * @version 1.0.0
 * @param <T>
 */
public interface ZookeeperCallback<T> {
	
	public T callback() throws Exception;

	public String getLockPath();
}
