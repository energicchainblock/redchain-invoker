package com.utsoft.blockchain.core.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.core.dao.mapper.FabricCaUserMapper;
import com.utsoft.blockchain.core.dao.model.FabricCaUserPo;
import com.utsoft.blockchain.core.fabric.ca.CaClientManager;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;
import com.utsoft.blockchain.core.service.ICaUserService;
import com.utsoft.blockchain.core.service.LocalKeyPrivateStoreService;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.IGlobals;
import com.utsoft.blockchain.core.util.SystemExceptionHandler;
import tk.mybatis.mapper.entity.Example;
/**
 * 用户ca 信息获取
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
@Service
public class CaUserServiceImpl implements ICaUserService {

	private CaClientManager caClientManager = CaClientManager.getIntance();
	
	@Autowired
	protected LocalKeyPrivateStoreService  localKeyPrivateStoreService;
	
	@Autowired
	private FabricCaUserMapper fabricCaUserMapper;
	/**
	 * 默认加解密工具
	 */
	private FamilySecCrypto familySecCrypto;
	
	@PostConstruct
	public void install() {
		familySecCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		try {
			caClientManager.serviceInstall(localKeyPrivateStoreService);
			Example example = new Example(FabricCaUserPo.class);
			String admin = IGlobals.getProperty("ca.root.admin","admin");
			example.createCriteria().andEqualTo("userName", admin);
			List<FabricCaUserPo> userlist = fabricCaUserMapper.selectByExample(example);
			if (CommonUtil.isCollectNotEmpty(userlist)) {
				
				FabricCaUserPo fabricCaUserPo = userlist.get(0);
				FabricAuthorizedUser adminUser = new FabricAuthorizedUser(fabricCaUserPo.getUserName(),fabricCaUserPo.getOrganization(),fabricCaUserPo.getStatus(),localKeyPrivateStoreService);
				adminUser.setAccount(fabricCaUserPo.getAccount());
				adminUser.setAffiliation(fabricCaUserPo.getAffiliation());
				adminUser.setEnrollmentSecret(fabricCaUserPo.getEnrollmentSecret());
				adminUser.setMspId(fabricCaUserPo.getMspId());
				if (caClientManager.adminInstall(adminUser)) {
					fabricCaUserMapper.updateFabricUserStatus(adminUser.getName());
				}
			}
		 } catch (Exception ex) {
			SystemExceptionHandler.getInstance().handlerException(ex);
		}
	}
	
	
	@Override
	public UserInfoRspModel registerAndEnroll(String userName, String partOforg,String affliation,String password) throws ServiceProcessException{
		
		Example example = new Example(FabricCaUserPo.class);
		example.createCriteria().andEqualTo("userName", userName);
		List<FabricCaUserPo> userlist = fabricCaUserMapper.selectByExample(example);
		if (CommonUtil.isCollectEmpty(userlist)) {
		 
			FabricAuthorizedUser fabricAuthorizedUser = caClientManager.registerUser(userName, partOforg, affliation, password);
			if (fabricAuthorizedUser!=null) {
				Map<String, String> properties = new HashMap<String,String>();
				properties.put("profile",IGlobals.getProperty("ca.user.profile",""));
				properties.put("label",IGlobals.getProperty("ca.user.label",""));
				if (caClientManager.enrollUser(fabricAuthorizedUser, properties)) {
					
					FabricCaUserPo  fabricCaUserPo = new FabricCaUserPo();
					fabricCaUserPo.setUserName(userName);
					fabricCaUserPo.setAccount(fabricAuthorizedUser.getAccount());
					fabricCaUserPo.setEnrollmentSecret(fabricAuthorizedUser.getEnrollmentSecret());
					fabricCaUserPo.setAffiliation(fabricAuthorizedUser.getAffiliation());
					fabricCaUserPo.setGmtCreate(new Date());
					fabricCaUserPo.setCert(fabricAuthorizedUser.getEnrollment().getCert());
					fabricCaUserPo.setMspId(fabricAuthorizedUser.getMspId());
					fabricCaUserMapper.insert(fabricCaUserPo);
					
				    FabricAuthorizedUser user = new FabricAuthorizedUser(fabricCaUserPo.getUserName(),fabricCaUserPo.getOrganization(),localKeyPrivateStoreService);
					if (user.getEnrollment()==null) {
						  throw new ServiceProcessException("user not enroll");
					  }
					 String privateKey;
					try {
						privateKey = familySecCrypto.convertPrivatelicKey(user.getEnrollment().getKey());
					} catch (CryptionException e) {
						 throw new ServiceProcessException("user private key is not convert");
					}
					UserInfoRspModel userInfo = new UserInfoRspModel();
					userInfo.setPrivateKey(privateKey);
					userInfo.setPassword(fabricAuthorizedUser.getEnrollmentSecret());
					return userInfo;
				}
			}	
		}
		return null;
	}

	@Override
	public UserInfoRspModel getUserInfo(String userName, String password) throws ServiceProcessException{
		
		UserInfoRspModel caUserInfoDto = null;
		Example example = new Example(FabricCaUserPo.class);
		example.createCriteria().andEqualTo("userName",userName);
		List<FabricCaUserPo> userlist = fabricCaUserMapper.selectByExample(example);
		if (CommonUtil.isCollectNotEmpty(userlist)) {
			
			 Optional<FabricCaUserPo>  fabricOpational = userlist.stream().filter((s) -> password.equalsIgnoreCase(s.getEnrollmentSecret())).findFirst();
			 if (fabricOpational.isPresent())
			 {   
				 
				  FabricCaUserPo userPo =  fabricOpational.get();
				  FabricAuthorizedUser user = new FabricAuthorizedUser(userPo.getUserName(),userPo.getOrganization(),localKeyPrivateStoreService);
				  if (user.getEnrollment()==null) {
					  throw new ServiceProcessException("user not enroll");
				  }
				 String privateKey;
				 try {
					privateKey = familySecCrypto.convertPrivatelicKey(user.getEnrollment().getKey());
				 } catch (CryptionException e) {
					 throw new ServiceProcessException("user private key is not convert");
				 }
				  caUserInfoDto = new UserInfoRspModel();
			      caUserInfoDto.setPassword(password);
			      caUserInfoDto.setPrivateKey(privateKey);
		      }
		  }
		 return caUserInfoDto;
	 }

	@Override
	public FabricAuthorizedUser getFabricUser(String username) {
		
		Example example = new Example(FabricCaUserPo.class);
		example.createCriteria().andEqualTo("userName",username);
		List<FabricCaUserPo> userlist = fabricCaUserMapper.selectByExample(example);
		if (CommonUtil.isCollectNotEmpty(userlist)) {
			  FabricCaUserPo userPo = userlist.get(0);
			  return  new FabricAuthorizedUser(userPo.getUserName(),userPo.getOrganization(),localKeyPrivateStoreService);
		}
		return null;
	}
}
