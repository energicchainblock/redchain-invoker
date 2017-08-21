package com.utsoft.blockchain.core.service;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.springframework.beans.factory.annotation.Autowired;
import com.utsoft.blockchain.core.dao.mapper.ChaincodeAccessCodeMapper;
import com.utsoft.blockchain.core.dao.mapper.ChaincodeMapper;
import com.utsoft.blockchain.core.dao.mapper.ChaincodeOrgConfigMapper;
import com.utsoft.blockchain.core.dao.model.ChaincodeAccessCodePo;
import com.utsoft.blockchain.core.dao.model.ChaincodeOrgConfigPo;
import com.utsoft.blockchain.core.dao.model.ChaincodePo;
import com.utsoft.blockchain.core.fabric.channel.ChannelClientPoolManager;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedOrg;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.FormatUtil;
import com.utsoft.blockchain.core.util.IGlobals;
import com.utsoft.blockchain.core.util.SystemExceptionHandler;
import tk.mybatis.mapper.entity.Example;
/**
 * 把与交易无关代码抽象到父类
 * @author hunterfox
 * @date: 2017年7月29日
 * @version 1.0.0
 */
public abstract class AbstractTkcBasicService {

	protected ChannelClientPoolManager chaincodeManager = ChannelClientPoolManager.getInstance();
	/**
	 * 关键信息存储
	 */
	@Autowired
	protected LocalKeyPrivateStoreService  localKeyPrivateStoreService;
	
	@Autowired
	protected ChaincodeMapper chaincodeMapper;
	
	@Autowired
	protected ChaincodeOrgConfigMapper chaincodeOrgConfigMapper;
	
	/**
	 * 交易指令mapper
	 */
	@Autowired
	protected ChaincodeAccessCodeMapper chaincodeAccessCodeMapper;
	
	/**
	 * 交易指令地图 
	 * <code>applycode --> ChaincodePo</code>
	 */
	protected static HashMap<String,Integer>  chainIdCodeMap = new HashMap<>();
	
	protected static HashMap<Integer,ChaincodeID>  chaincodeMap = new HashMap<>();
	
	/**
	 * 初始化交易信息及链接配置信息
	 */
	public void init() {
		
		
		Example example = new Example(ChaincodeAccessCodePo.class);
		example.createCriteria().andEqualTo("status",1);
		List<ChaincodeAccessCodePo> acesscodePoList =  chaincodeAccessCodeMapper.selectByExample(example);
		acesscodePoList.stream().forEach(code -> {
			chainIdCodeMap.put(code.getApplyCode(), code.getChainId());
		});
		
		example = new Example(ChaincodePo.class);
		example.createCriteria().andEqualTo("status",1);
		List<ChaincodePo> chaincodePoList = chaincodeMapper.selectByExample(example);
	    if (CommonUtil.isCollectNotEmpty(chaincodePoList)) {
	   
	    	  chaincodePoList.stream().filter(p-> !chaincodeMap.containsKey(p.getChainId())).forEach(chaincodePo -> {
	    		
	    		     ChaincodeID  chaincodeID = ChaincodeID.newBuilder().setName(chaincodePo.getChainCodeName())
	                  .setVersion(chaincodePo.getCodeVersion())
	                 .setPath(chaincodePo.getCodePath()).build();
	    		    chaincodeMap.put(chaincodePo.getChainId(), chaincodeID);
	      });
	    }
	   
		/**
		 * 获取组织地址配置
		 */
	    chaincodeMap.forEach((chainId,chaincodeID)->{
	    	  
	    	List<ChaincodeOrgConfigPo> chainOrgList =chaincodeOrgConfigMapper.listNodeConfigAddress(chainId);  
	    	chainOrgList.stream().forEach(chainOrgpo -> {
	    		
	    		/**
	    		 * 配置组织信息
	    		 */
	    		  FabricAuthorizedOrg fabricAuthorizedOrg = new FabricAuthorizedOrg(chainOrgpo.getOrgname(),chainOrgpo.getMspId(),chainOrgpo.getChannelName());
	    		  fabricAuthorizedOrg.setDomainName(chainOrgpo.getDomainName());
	    		  
	    		  String [] ps = chainOrgpo.getOrdererLocations().split("[ \t]*,[ \t]*");
	              for (String peer : ps) {
	                
	            	 String[] nl = peer.split("[ \t]*@[ \t]*");
	                
	                 fabricAuthorizedOrg.addOrdererLocation(nl[0], CommonUtil.grpcTLSify(nl[1]));
	               }
	    		 
	               ps = chainOrgpo.getPeerLocations().split("[ \t]*,[ \t]*");
	               for (String peer : ps) {
	                    String[] nl = peer.split("[ \t]*@[ \t]*");
	                    fabricAuthorizedOrg.addPeerLocation(nl[0], CommonUtil.grpcTLSify(nl[1]));
	               }

	                String eventHubNames = chainOrgpo.getEventhubLocations();
	                ps = eventHubNames.split("[ \t]*,[ \t]*");
	                for (String peer : ps) {
	                    String[] nl = peer.split("[ \t]*@[ \t]*");
	                    fabricAuthorizedOrg.addEventHubLocation(nl[0], CommonUtil.grpcTLSify(nl[1]));
	                }
	                fabricAuthorizedOrg.setCALocation(CommonUtil.httpTLSify(chainOrgpo.getCaLocations()));
	                
	                final String peerRootPath = IGlobals.getProperty("fabric.peer_root_path");
	                if(IGlobals.getBooleanProperty("ca.runningFabricCATLS", false)) {
	                	
	                	 String cert = peerRootPath+"/DNAME/ca/ca.DNAME-cert.pem".replaceAll("DNAME", chainOrgpo.getDomainName());
	                	 File cf = CommonUtil.getFilepath(cert);
	                     Properties properties = new Properties();
	                     properties.setProperty("pemFile", cf.getAbsolutePath());
	                   //testing environment only NOT FOR PRODUCTION!
	                     properties.setProperty("allowAllHostNames", "true"); 
	                     fabricAuthorizedOrg.setCAProperties(properties);
	                }
	                
	                
	                StringBuilder sb = new StringBuilder();
	                sb.append(peerRootPath);
	                sb.append(chainOrgpo.getDomainName()).append("/");
	                sb.append(FormatUtil.formater("/users/Admin@%s/msp/keystore", chainOrgpo.getDomainName()));
	                try {
	                     
	                	 File findFile = CommonUtil.getFilepath(sb.toString());
	                	 File privateKey = CommonUtil.findFileSk(findFile);
	                	/**
	                	 * 获取公约
	                	 */
	                    sb.delete(0, sb.length()); 
	                    sb.append(peerRootPath);
		                sb.append(chainOrgpo.getDomainName()).append("/");
		                sb.append(FormatUtil.formater("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", chainOrgpo.getDomainName(),chainOrgpo.getDomainName()));
	                   
		           	    File certPublicKey = CommonUtil.getFilepath(sb.toString());
		               
	                    FabricAuthorizedUser peerOrgAdmin = localKeyPrivateStoreService.getMember(chainOrgpo.getOrgname() + "Admin", chainOrgpo.getOrgname(),chainOrgpo.getMspId(),
	                		privateKey,certPublicKey);
	                     //A special user that can create channels, join peers and install chaincode
	                     fabricAuthorizedOrg.setPeerAdmin(peerOrgAdmin); 
	                   
	                     /**
	                      * 注册配置信息
	                      */
	                    chaincodeManager.registerChannelConfig(chaincodeID, fabricAuthorizedOrg);
					   // chaincodeManager.start(chaincodeID,fabricAuthorizedOrg);
				    } catch (Exception ex) {
					   SystemExceptionHandler.getInstance().handlerException(ex);
				  } 
	    	});
	    }); 
	}
	
	/**
	 * 访问交易链码
	 * @param applyCode
	 * @return
	 */
	protected ChaincodeID getChainCode(String applyCode) {
		Integer chainId = chainIdCodeMap.get(applyCode);
		return chaincodeMap.get(chainId);
	}
}
