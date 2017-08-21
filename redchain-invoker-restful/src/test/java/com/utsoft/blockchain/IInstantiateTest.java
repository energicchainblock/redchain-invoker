package com.utsoft.blockchain;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.utsoft.blockchain.core.fabric.channel.ChannelClientPoolManager;
import com.utsoft.blockchain.core.util.CommonUtil;
@Profile({"test"}) 
@ContextConfiguration(classes = { TkcInvokerApplicationServer.class })
@RunWith(SpringRunner.class)
@SpringBootTest()
public class IInstantiateTest {

	
	ChannelClientPoolManager clientManager = ChannelClientPoolManager.getInstance();
    ChaincodeID  chaincodeID;
	
	 @Before
	public void setup() {

		String chainCodeName = "example_cc_go";
		String version = "1";
		String codePath = "github.com/example_cc";
		chaincodeID = ChaincodeID.newBuilder().setName(chainCodeName)
                 .setVersion(version)
                .setPath(codePath).build();
	}
	 
	 
	 @Test
	 public void testInitialChannel() {
		File txPath = CommonUtil.getFilepath("channel/channel.tx");
		boolean isChannel = clientManager.initNewChannl(chaincodeID, txPath);
		Assert.assertEquals(true, isChannel);
	}
	 
	
	@Test
	public void testInstallProgammChain() {
		
		 File installPath =  CommonUtil.getFilepath("channel/gocc/sample1");
		 clientManager.installChaincodeInOrganization(chaincodeID, installPath);
	}
	
	@Test
	public void testInstantiateProgammChain() {
		
		 File dorsementpolicyFile =  CommonUtil.getFilepath("channel/dorsementpolicy.yaml");
		 List<String> inits = new ArrayList<>();
		 inits.add("wangbo");
		 inits.add("500");
		 inits.add("hunterfox");
		 inits.add("600");
		clientManager.instantiateChaincodeInOrganization(chaincodeID, dorsementpolicyFile, inits);
	}
}
