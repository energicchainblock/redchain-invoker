package com.utsoft.blockchain.api.proivder;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.ServiceApplyCodeReqMode;
import com.utsoft.blockchain.api.pojo.UserInfoRequstModel;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.weibo.api.motan.transport.async.MotanAsync;
/**
 * 用户信息及申请信息
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
@MotanAsync
public interface ITkcAccountStoreExportService {

	
	/**
	 * 用户请求注册信息
	 * @param requestModel
	 * @return
	 */
	public BaseResponseModel<UserInfoRspModel> register(UserInfoRequstModel requestModel);
	
	/**
	 * 返回用户关键信息，后期需求调整，可能 todo
	 * @param username
	 * @param token
	 * @return 返回用户关键信息
	 */
	public  BaseResponseModel<UserInfoRspModel> getIndividualAccout(String username,String token);
	
	/**
	 * 服务器申请号码开通
	 * @param token 申请用户
	 * @param serviceApplyCodeReqMode 申请内容
	 * @return 成功200,否则失败
	 */
	public  BaseResponseModel<Integer> applyService(String token,ServiceApplyCodeReqMode serviceApplyCodeReqMode ); 
}
