package com.utsoft.blockchain.core.rpc.provider;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.UserInfoRequstModel;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.utsoft.blockchain.api.proivder.ITkcAccountStoreExportService;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.core.rpc.AbstractTkcRpcBasicService;
import com.utsoft.blockchain.core.service.ICaUserService;
import com.utsoft.blockchain.core.service.impl.RedisRepository;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.FormatUtil;
import com.utsoft.blockchain.core.util.IGlobals;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
/**
 * 注册登陆及获公钥
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
@MotanService(export = "tkcExportServer:8002")
public class TkcAccountStoreExportService extends AbstractTkcRpcBasicService implements ITkcAccountStoreExportService {

	@Autowired
	private ICaUserService caUserService;
	
     @Autowired
	private RedisRepository<String,UserInfoRequstModel> redisRepository;
	 
     @Override
	public BaseResponseModel<UserInfoRspModel> register(UserInfoRequstModel requestModel) {
		
		BaseResponseModel<UserInfoRspModel>  rspModel = BaseResponseModel.build();
		if (CommonUtil.isEmpty(requestModel.getUserName(),requestModel.getCreated(),requestModel.getPassword()) ){
		    return rspModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		synchronized (requestModel.getCreated()) {
			try {
				
			 String userPrefix = FormatUtil.redisPrefix(requestModel.getUserName(),requestModel.getCreated());
			 UserInfoRequstModel redisObject = redisRepository.get(userPrefix);
			 if (redisObject==null) {
				 redisRepository.set(userPrefix,requestModel,120L,TimeUnit.SECONDS);
			} else {
				rspModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return rspModel;
			}
			String affliation = IGlobals.getProperty("ca.affliation","org1.department1");
			String partOforg = IGlobals.getProperty("ca.org.name","org1");
			
		    UserInfoRspModel result = caUserService.registerAndEnroll(requestModel.getUserName(),partOforg, affliation,requestModel.getPassword());
			if (result != null) {
				   rspModel.setData(result);
			   } else 
			     rspModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
			} catch (Exception ex) {
				rspModel.setCode(Constants.SEVER_INNER_ERROR);
				rspModel.setMessage(ex.getMessage());
				Object[] args = {requestModel,ex};
				logger.error("erros:{} :{}",args );
		   }
		  return rspModel;
		}	
	}

	@Override
	public BaseResponseModel<UserInfoRspModel> getIndividualAccout(String username,String token) {
		
		BaseResponseModel<UserInfoRspModel>  rspModel = BaseResponseModel.build();
		if (CommonUtil.isEmpty(username,token) ){
			return rspModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		try {
		    UserInfoRspModel result = caUserService.getUserInfo(username, token);
			 if (result == null) {
				rspModel.setCode(Constants.ITEM_NOT_FIND);
			} else rspModel.setData(result);
		} catch (Exception ex) {
			rspModel.setCode(Constants.SEVER_INNER_ERROR);
			Object[] args = {username,ex};
			logger.error("erros:{} :{}",args );
			rspModel.setMessage(ex.getMessage());
		 }
		return rspModel;
	}
}
