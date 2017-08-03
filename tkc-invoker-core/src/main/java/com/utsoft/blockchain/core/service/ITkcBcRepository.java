package com.utsoft.blockchain.core.service;

import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoDto;

/**
 *  tkc-blockchain basic operation
 *  <ul>
 *   <li>basic query example : block info,and transaction info </li>
 *   <li>install and other manager operator</li>
 *   <li>query global info</li>
 *  </ul>
 * @author hunterfox
 * @date: 2017年7月31日
 * @version 1.0.0
 */
public interface ITkcBcRepository {

	
	/**
	 * 查询区块链事物信息
	 * @param applycode 业务代码
	 * @return
	 */
	public TkcTransactionBlockInfoDto queryTransactionBlockByID(String applycode,String txtId) throws ServiceProcessException; 
	
	/**
	 * 查询block 信息
	 * @param applycode
	 * @param txtId
	 * @return
	 * @throws ServiceProcessException
	 *//*
	public TkcTransactionInfoDto queryBlockByTransactionID(String applycode,String txtId) throws ServiceProcessException; */
}
