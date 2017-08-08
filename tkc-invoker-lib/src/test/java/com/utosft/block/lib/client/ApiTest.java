package com.utosft.block.lib.client;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoVo;
import com.utsoft.blockchain.api.pojo.TransactionVarModel;
import com.utsoft.blockchain.api.pojo.UserInfoRequstModel;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.utsoft.blockchain.api.proivder.ITkcAccountStoreExportService;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.utsoft.blockchain.lib.ServiceClientApplication;
@RunWith(SpringJUnit4ClassRunner.class)     
@ContextConfiguration(initializers={ConfigFileApplicationContextInitializer.class})
@SpringBootTest(classes=ServiceClientApplication.class)
public class ApiTest {

	
	@Value("${applyCategory}")
	private String applyCategory;
	 
	@Autowired
	ITkcTransactionExportService tkcTransactionExportService;
	
	@Autowired
	private ITkcAccountStoreExportService tkcAccountStoreExportService;
	
	 private String privateKey;
	 
	 @Value("${username}")
	 private String username;
	 
	 @Value("${password}")
	 private String password ;
	 
	 @Value("${toUser}")
	 private String toUser;
	 
	 private static String txId ="53726fda86a9f4a3f1c7a4580f8f168829ef64a4e405c7fb7a2fe28c3704e8bb";
	@Before
	public void setup() {
		
		BaseResponseModel<UserInfoRspModel> userPrivateKeyAccess = tkcAccountStoreExportService.getIndividualAccout(username,password);
	    if (userPrivateKeyAccess.isSuccess()) {
		    privateKey = userPrivateKeyAccess.getData().getPrivateKey();
	    } else   {
	    	 String  created = SdkUtil.generateId();
		    
	    	 UserInfoRequstModel requestModel = new UserInfoRequstModel();
			 requestModel.setUserName(username);
			 requestModel.setPassword(password);
			 requestModel.setCreated(created);
			
			BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.register(requestModel);
			if (baseResponse.getData()!=null) {
				String publicKey = baseResponse.getData().getPrivateKey();
				String password =baseResponse.getData().getPassword();
				System.out.println(password+":"+publicKey);
			}
			userPrivateKeyAccess = tkcAccountStoreExportService.getIndividualAccout(username,password);
			if (!userPrivateKeyAccess.isSuccess()) {
				fail("user not exists");
		    }
			 privateKey = userPrivateKeyAccess.getData().getPrivateKey();
	   }
	}
	 
	@Test
	public void testQueryAccoutInfo() {
		
		FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		SignaturePlayload signaturePlayload = new SignaturePlayload(familyCrypto);
	
		String created = SdkUtil.generateId();
		String from = username;
		
		/**
		 * 注意顺序
		 */
		signaturePlayload.addPlayload(applyCategory);
		signaturePlayload.addPlayload(from);
		signaturePlayload.addPlayload(created);
		
		 String sign;
		 try {
			sign = signaturePlayload.doSignature(privateKey);
		  } catch (CryptionException e) {
			e.printStackTrace();
			fail("not sign success ");
			return ;
		}
		
		BaseResponseModel<TkcQueryDetailRspVo> baseResponse = tkcTransactionExportService.getTransactionDetail(applyCategory, from, created, sign);
		System.out.println(JSON.toJSON(baseResponse.getData()));
		assertEquals(baseResponse.getCode(),"200");
	}
	
	
	@Test
	public void testRegister() {
		String  created = SdkUtil.generateId();
		UserInfoRequstModel requestModel = new UserInfoRequstModel();
		requestModel.setUserName(username);
		requestModel.setPassword(password);
		requestModel.setCreated(created);
		
		BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.register(requestModel);
		if (baseResponse.getData()!=null) {
			String publicKey = baseResponse.getData().getPrivateKey();
			String password =baseResponse.getData().getPassword();
			System.out.println(password+":"+publicKey);
		}
	}
	
	@Test
	public void testGetPublicKey() {
		
		BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.getIndividualAccout(username,password);
		if (baseResponse.getData()!=null){
			String publicKey = baseResponse.getData().getPrivateKey();
			String password = baseResponse.getData().getPassword();	
			System.out.println(password+":"+publicKey);
		}
		assertEquals(baseResponse.getCode(),"200");
	}	
	
	
	/**
	 * 支付
	 */
	@Test
	public void testMoveAToB() {
		
		String to = toUser;
		String submitJson ="100";
		String created = SdkUtil.generateId();
		
		TransactionVarModel model = new TransactionVarModel(applyCategory);
		model.setCreated(created);
		model.setFrom(username);
		model.setTo(to);
		model.setSubmitJson("100");
		
		FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		SignaturePlayload signaturePlayload = new SignaturePlayload(familyCrypto);
		String from = username;
		/**
		 * md5(applyCategory=1&from=2&to=3&cmd=4&submitJson=5&created=xxx)
		 * 注意顺序
		 */
		signaturePlayload.addPlayload(applyCategory);
		signaturePlayload.addPlayload(from);
		signaturePlayload.addPlayload(to);
		signaturePlayload.addPlayload(model.getCmd());
		signaturePlayload.addPlayload(submitJson);
		signaturePlayload.addPlayload(created);
		String sign;
		 try {
			sign = signaturePlayload.doSignature(privateKey);
		  } catch (CryptionException e) {
			e.printStackTrace();
			fail("not sign success ");
			return ;
		}
		 BaseResponseModel<TkcSubmitRspVo> baseResponse = tkcTransactionExportService.tranfer(model,sign);
		 assertEquals(baseResponse.getCode(),200);
		 if (baseResponse.isSuccess()) {
			 txId = baseResponse.getData().getTxId();
			 System.out.println(JSON.toJSON(baseResponse.getData()));
		 }
		 
	}
	
	/**
	 * 
	 */
	@Test
	public void queryTxBlockInfo() {
	
		FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		SignaturePlayload signaturePlayload = new SignaturePlayload(familyCrypto);
	
		String created = SdkUtil.generateId();
		String from = username;
		
		/**
		 * 注意顺序
		 */
		signaturePlayload.addPlayload(applyCategory);
		signaturePlayload.addPlayload(from);
		signaturePlayload.addPlayload(txId);
		signaturePlayload.addPlayload(created);
		
		 String sign;
		 try {
			sign = signaturePlayload.doSignature(privateKey);
		  } catch (CryptionException e) {
			e.printStackTrace();
			fail("not sign success ");
			return ;
		}
		 
	   BaseResponseModel<TkcTransactionBlockInfoVo> baseResponse = tkcTransactionExportService.listStockChanges(applyCategory,from, txId, created, sign);
	   assertEquals(baseResponse.getCode(),200);
	   if (baseResponse.isSuccess()) {
		   System.out.println(JSON.toJSON(baseResponse.getData()));
	   }
	}	
}
