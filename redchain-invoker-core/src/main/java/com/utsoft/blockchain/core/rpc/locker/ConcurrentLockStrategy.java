package com.utsoft.blockchain.core.rpc.locker;

import com.utsoft.blockchain.api.exception.ServiceProcessException;

public interface ConcurrentLockStrategy {

	/**
	 * 申请锁定单地址
	 * @param address
	 * @return
	 */
	public String applyTransactionToken(String address) throws ServiceProcessException ;
	/**
	 * 申请锁定双地址
	 * @param address
	 * @param from
	 * @return
	 */
	public String applyTransactionToken(String address,String from) throws ServiceProcessException;
	
	/**
	 * 释放地址
	 * @param address
	 * @return
	 */
	public void releaseToken(String address);
	
	/**
	 * 地址是否可用
	 * @param address
	 * @return
	 */
	public boolean isAddressAvailable(String address);
}
