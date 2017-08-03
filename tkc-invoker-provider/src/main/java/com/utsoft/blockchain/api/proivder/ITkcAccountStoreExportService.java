package com.utsoft.blockchain.api.proivder;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.UserInfoRequstModel;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
/**
 * 用户信息
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
public interface ITkcAccountStoreExportService {

	
	/**
	 * 用户请求注册信息
	 * @param created 请求时间序列
	 * @param requestModel
	 * @return
	 */
	public BaseResponseModel<UserInfoRspModel> register(String created,UserInfoRequstModel requestModel);
	
	/**
	 * 返回用户关键信息，后期需求调整，可能 todo
	 * @param username
	 * @param password
	 * @return 返回用户关键信息
	 */
	public  BaseResponseModel<UserInfoRspModel> getIndividualAccout(String username,String password);
}
