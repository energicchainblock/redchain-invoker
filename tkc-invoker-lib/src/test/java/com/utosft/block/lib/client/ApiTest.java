package com.utosft.block.lib.client;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.UserInfoRequstModel;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.utsoft.blockchain.api.proivder.ITkcAccountStoreExportService;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.lib.ServiceClientApplication;
@RunWith(SpringJUnit4ClassRunner.class)     
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
	 private String username ;
	 
	 @Value("${username}")
	 private String password ;
	 
	@Before
	public void setup() {
		
		BaseResponseModel<UserInfoRspModel> userPrivateKeyAccess = tkcAccountStoreExportService.getIndividualAccout(username,password);
	    if (userPrivateKeyAccess.isSuccess()) {
		    privateKey = userPrivateKeyAccess.getData().getPrivateKey();
	    } else   {
		     UserInfoRequstModel requestModel = new UserInfoRequstModel();
			 requestModel.setUserName(username);
			 requestModel.setPassword(password);
			 String  created = SdkUtil.generateId();
			BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.register(created,requestModel);
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
		System.out.println(baseResponse);
		assertEquals(baseResponse.getCode(),"200");
	}
	
	
	@Test
	public void testRegister() {
		
		UserInfoRequstModel requestModel = new UserInfoRequstModel();
		requestModel.setUserName(username);
		requestModel.setPassword(password);
		String  created = SdkUtil.generateId();
		BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.register(created,requestModel);
		if (baseResponse.getData()!=null) {
			String publicKey = baseResponse.getData().getPrivateKey();
			String password =baseResponse.getData().getPassword();
			System.out.println(password+":"+publicKey);
		}
		assertEquals(baseResponse.getCode(),"200");
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
}
