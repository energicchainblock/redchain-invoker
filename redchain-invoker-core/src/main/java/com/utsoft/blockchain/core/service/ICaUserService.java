package com.utsoft.blockchain.core.service;

import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;

/**
 * 用户认证系统，保存ca 公私钥
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
public interface ICaUserService {

	/**
	 * 用户注册到ca 系统
	 * @param userName
	 * @param partOforg
	 * @param affliation 
	 * @param password
	 * @return
	 * @throws ServiceProcessException
	 */
	public UserInfoRspModel registerAndEnroll(String userName,String partOforg,String affliation,String password) throws ServiceProcessException;
	
	/**
	 * 用户通过密码获取公钥
	 * @param userName
	 * @param token
	 * @throws ServiceProcessException
	 * @return
	 */
	public UserInfoRspModel getUserInfo (String userName,String token) throws ServiceProcessException;
		
	/**
	 * 获取fabric ca 用户认证
	 * @param username
	 * @return
	 */
	public FabricAuthorizedUser getFabricUser(String username);
	
}
