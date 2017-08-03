package com.utsoft.blockchain.core.service;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.pojo.SubmitRspResultDto;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.util.TransactionCmd;
/**
 * 交易相关和查询
 * @author hunterfox
 * @date 2017年7月28日
 * @version 1.0.0
 */
public interface ITransactionService {
	
	 /**
	  *  交易链初始化
	  */
	 public void construtChainCode();
	
	 /**
	  * 交易转账
	  *@param applyCode 交易代码
	 * @param cmd 交易命令
	 * @param from  转出账户
	 * @param to  转入账户
	 * @param submitJson 提交内容
	 * @param created
	 * @return submit result 
	 * @throws ServiceProcessException
	 */
	public SubmitRspResultDto tranfer(String applyCode,String from,String to,TransactionCmd cmd, String submitJson,String created) throws ServiceProcessException;
	  
	
	/**
	 * 查询个人账户账户记录
	 * @param applyCode 交易代码
	 * @param from 来源账号
	 * @param cmd
	 * @param created
	 * @return
	 */
	TkcQueryDetailRspVo select(String applyCode,String from,TransactionCmd cmd,String created) throws ServiceProcessException;

	/**
	 * 带条件查询个人信息
	 * @param applyCode
	 * @param account_to
	 * @param cmd
	 * @param submitJson
	 * @param created
	 * @return
	 * @throws ServiceProcessException
	 */
	public TkcQueryDetailRspVo selectByJson(String applyCode,String account_to, TransactionCmd cmd, String submitJson, String created)
			throws ServiceProcessException;
}
