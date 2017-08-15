package com.utsoft.blockchain.core.fabric.channel;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.pojo.ReqtOrderDto;
import com.utsoft.blockchain.api.pojo.ReqtQueryOrderDto;
import com.utsoft.blockchain.api.pojo.RspQueryResultDto;
import com.utsoft.blockchain.api.pojo.SubmitRspResultDto;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoDto;
import com.utsoft.blockchain.core.fabric.GobalFabricMapStore;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedOrg;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.Constants;
import com.utsoft.blockchain.core.util.IGlobals;
/**
 * 区块链代理及配置manager
 * @author hunterfox
 * @date: 2017年7月28日
 * @version  1.0.0
 */
public class ChannelClientPoolManager {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private  static ChannelClientPoolManager channelManager = new ChannelClientPoolManager();
	
	private GobalFabricMapStore orgsConfigMap  = GobalFabricMapStore.getInstance();
	/**
	 * 通信封装
	 */
	private ChannelClientProxy channelClientProxy = new ChannelClientProxy();
	/**
	 * client 封装
	 */
	private  HFClient client = HFClient.createNewInstance();
	 
	public ChannelClientPoolManager() {
		 try {
				client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
			} catch (CryptoException e) {
				Object[] agrs = {e};
				logger.error("connectChannel ChannelClientPoolManager init errors:{}",agrs);
			} catch (InvalidArgumentException e) {
				Object[] agrs = {e};
				logger.error("connectChannel ChannelClientPoolManager init errors:{}",agrs);
			}
	}
	
	public static ChannelClientPoolManager getInstance() {
		return channelManager;
	}
	

	/**
	 * 判断channel 是否可用
	 * @param chaincodeID
	 * @return
	 */
	public boolean checkChannelActive(ChaincodeID chaincodeID) {
		Channel channel = getChannel(chaincodeID);
		return channel!=null && !channel.isShutdown();
	}
	
	/**
	 * 注册配置信息
	 * @param chaincodeID
	 * @param nodeConfig
	 */
	public void registerChannelConfig(ChaincodeID chaincodeID,FabricAuthorizedOrg nodeConfig) {
		orgsConfigMap.putChainOrgConfig(chaincodeID, nodeConfig);
	}
	/**
	 * 获取链接
	 * @param chaincodeID
	 * @return
	 */
	public Channel getChannel(ChaincodeID chaincodeID) {
		FabricAuthorizedOrg orgconfig = orgsConfigMap.getOrgConfigByccId(chaincodeID);
		if (orgconfig==null) return null;
		return client.getChannel(orgconfig.getChannelName());
	}
	
	/**
	 *  提交交易请求
	 * @param newChannel
	 * @param chaincodeID
	 * @param order
	 * @return
	 */
 	 public SubmitRspResultDto submitRequest(ChaincodeID chaincodeID,ReqtOrderDto order) {
 		   Channel channel = getChannel(chaincodeID);
	       int invokeWaitTime =  IGlobals.getIntProperty(Constants.INVOKEWAITTIME, 10000);
			try {
				return  channelClientProxy.submitRequest(client,channel, chaincodeID, order)
				   .thenApply(transactionEvent -> {
					
					    SubmitRspResultDto result = new SubmitRspResultDto();
					    result.setStatus(transactionEvent.isValid());
					    String testTxID = transactionEvent.getTransactionID(); 
						result.setTxId(testTxID);
					  
					  return result;
				   }).exceptionally(e -> {
					  
					  Object[] agrs = {chaincodeID,order,e};
				      logger.error("submitRequest chaincode:{}  order{} and errors:{}",agrs);
					 return null;
				  }).get(invokeWaitTime, TimeUnit.MILLISECONDS);
			} catch (Exception ex) {
				Object[] agrs = {order,ex};
				logger.error("submitRequest :request:{} and errors:{}",agrs);
				throw new ServiceProcessException("submitRequest request"+ order); 
			}
	 }
 	
 	 
  	public void start(ChaincodeID chaincodeID,FabricAuthorizedOrg orgconfig) {
 		try {
			channelClientProxy.connectChannel(client, orgconfig.getChannelName(),orgconfig,chaincodeID);
		} catch (Exception e) {
			Object[] agrs = {chaincodeID,orgconfig,e};
			logger.error("connectChannel chaincode:{}  orgconfig;{} and errors:{}",agrs);
			throw new ServiceProcessException("install channel:"+e); 
		}
 	}
  	
 	/**
 	 * 链接许可请求
 	 * @param name
 	 * @param sampleOrg
 	 */
 	public void connectChannel(ChaincodeID chaincodeID,FabricAuthorizedOrg orgconfig) {
 		try {
			channelClientProxy.connectChannel(client, orgconfig.getChannelName(),orgconfig,chaincodeID);
		} catch (Exception e) {
			Object[] agrs = {chaincodeID,orgconfig,e};
			logger.error("connectChannel chaincode:{}  orgconfig;{} and errors:{}",agrs);
		}
 	}
 	
 	/**
 	 * 自动重连
 	 * @param chaincodeID
 	 * @param orgconfig
 	 */
	public void reconnect(ChaincodeID chaincodeID) {
		 FabricAuthorizedOrg orgconfig = orgsConfigMap.getOrgConfigByccId(chaincodeID);
	 	try {
			channelClientProxy.connectChannel(client,orgconfig.getChannelName(),orgconfig,chaincodeID);
		} catch (Exception e) {
			Object[] agrs = {chaincodeID,orgconfig,e};
			logger.error("connectChannel chaincode:{}  orgconfig;{} and errors:{}",agrs);
		}
 	}
	
 	/**
 	 *  查询请求
 	 * @param chaincodeID 链码
 	 * @param reqtQueryOrderDto 请求内容
 	 * @return
 	 */
 	public RspQueryResultDto query(ChaincodeID chaincodeID,ReqtQueryOrderDto reqtQueryOrderDto) {
 		Channel channel = getChannel(chaincodeID);
 		return channelClientProxy.queryChaincode(client, channel,chaincodeID, reqtQueryOrderDto);
 	}	
 	
	/**
	 * 查询交易链码block 信息
	 * 
	 * @param chaincodeID
	 * @param testTxID
	 * @return 交易链码详细信息
	 */
	public TkcTransactionBlockInfoDto querySourceBlockByTransactionID(ChaincodeID chaincodeID, String testTxID)
			throws ServiceProcessException {
		Channel channel = getChannel(chaincodeID);

		TkcTransactionBlockInfoDto dto = new TkcTransactionBlockInfoDto();
		try {

			BlockchainInfo channelInfo = channel.queryBlockchainInfo();
			String chainCurrentHash = Hex.encodeHexString(channelInfo.getCurrentBlockHash());
			dto.setChainCurrentHash(chainCurrentHash);

			BlockInfo blockInfo = channel.queryBlockByTransactionID(testTxID);
			String previousHash = Hex.encodeHexString(blockInfo.getPreviousHash());
			String datahash = Hex.encodeHexString(blockInfo.getDataHash());
			long blockNumber = blockInfo.getBlockNumber();
			dto.setBlockNumber(blockNumber);
			dto.setPreviousHash(previousHash);

			TransactionInfo txInfo = channel.queryTransactionByID(testTxID);
			dto.setTxValCodeNumber(txInfo.getValidationCode().getNumber());
			dto.setDatahash(datahash);

			List<JSONObject> envelopeObjectList = new ArrayList<>();
			dto.getCommits().put("envelopes", envelopeObjectList);

			for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {

				JSONObject envelopeObject = new JSONObject();
				envelopeObject.put("epoch", envelopeInfo.getEpoch());
				envelopeObject.put("timestamp", envelopeInfo.getTimestamp());
				envelopeObject.put("channelId", envelopeInfo.getChannelId());
				envelopeObjectList.add(envelopeObject);

				if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {

					BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;

					List<List<JSONObject>> transactionActionInfoList = new ArrayList<>();
					envelopeObject.put("transactionActionInfoCount",
							transactionEnvelopeInfo.getTransactionActionInfoCount());
					envelopeObject.put("transactionActionInfoIsValid", transactionEnvelopeInfo.isValid());
					envelopeObject.put("validationCode", transactionEnvelopeInfo.getValidationCode());

					for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo
							.getTransactionActionInfos()) {

						TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
						if (null != rwsetInfo) {

							List<JSONObject> rwsetInfoObjects = new ArrayList<>();
							for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {

								final String namespace = nsRwsetInfo.getNaamespace();
								KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();

								JSONObject rwsObject = new JSONObject();
								List<JSONObject> readObjects = new ArrayList<>();
								for (KvRwset.KVRead readList : rws.getReadsList()) {

									JSONObject readObject = new JSONObject();
									readObject.put("read_version_block", readList.getVersion().getBlockNum());
									readObject.put("readKey", readList.getKey());
									readObject.put("readVersionNum", readList.getVersion().getTxNum());
									readObjects.add(readObject);
								}
								rwsObject.put("readLists", readObjects);

								/**
								 * 写数据
								 */
								List<JSONObject> writerObjects = new ArrayList<>();
								for (KvRwset.KVWrite writeList : rws.getWritesList()) {

									String valAsString = "";
									JSONObject writeObject = new JSONObject();
									try {
										valAsString = CommonUtil.printableString(
												new String(writeList.getValue().toByteArray(), "UTF-8"));
										writeObject.put("writevalue", valAsString);
										writerObjects.add(writeObject);
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
									writeObject.put("writekey", writeList.getKey());
									writeObject.put("writenamespace", namespace);
									rwsObject.put("writeLists", writeObject);
								}
								rwsObject.put("writerList", writerObjects);

								rwsetInfoObjects.add(rwsObject);
							}
							transactionActionInfoList.add(rwsetInfoObjects);
						}
					}
					envelopeObject.put("list", transactionActionInfoList);
				}
			}
		} catch (InvalidProtocolBufferException | InvalidArgumentException | ProposalException e) {
			throw new ServiceProcessException(com.utsoft.blockchain.api.util.Constants.SEVER_INNER_ERROR,"query block is error testTxID {" + testTxID + "} e:{} ", e);
		}
		return dto;
	}
 	
	public TransactionInfo queryBasicInfo(ChaincodeID chaincodeID,String txtId) {
		  Channel channel = getChannel(chaincodeID);
	      try {
			return channel.queryTransactionByID(txtId);
		  } catch (InvalidArgumentException | ProposalException e) {
			  throw new ServiceProcessException("queryBasicInfo byID:"+txtId+"with"+chaincodeID.getName());
		 }
	}
	
	/**
	 * 安装链码及执行程序
	 * @param chaincodeID
	 * @param installPath
	 */
	public void installChaincodeInOrganization(ChaincodeID chaincodeID,File installPath) {
		Channel channel = getChannel(chaincodeID);
		FabricAuthorizedOrg orgconfig = orgsConfigMap.getOrgByMatch(chaincodeID);
		if (orgconfig ==null)
		   throw new ServiceProcessException("install channel or fabricAuthorizedOrg not extis "+chaincodeID);
		channelClientProxy.install(client, channel, orgconfig, chaincodeID, installPath);
	}
	
	/**
	 * 实例化链码及程序
	 * @param chaincodeID
	 * @param dorsementpolicyFile
	 * @param objects
	 * @return
	 */
	public SubmitRspResultDto instantiateChaincodeInOrganization(ChaincodeID chaincodeID,File dorsementpolicyFile,List<String> objects) {
		int invokeWaitTime =  IGlobals.getIntProperty(Constants.INVOKEWAITTIME, 10000);
		Channel channel = getChannel(chaincodeID);
		FabricAuthorizedOrg orgconfig = orgsConfigMap.getOrgByMatch(chaincodeID);
		try {
			return channelClientProxy.instantiate(client, channel, dorsementpolicyFile, orgconfig, chaincodeID, objects)
			.thenApply(transactionEvent -> {
				
				    SubmitRspResultDto result = new SubmitRspResultDto();
				    result.setStatus(transactionEvent.isValid());
				    String testTxID = transactionEvent.getTransactionID(); 
					result.setTxId(testTxID);
				  
				  return result;
				  
			 }).exceptionally(e -> {
				 Object[] agrs = {chaincodeID,orgconfig,e};
				 logger.error("instantiate chaincode:{}  orgconfig;{} and errors:{}",agrs);
				 return null;
			  }).get(invokeWaitTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
		  throw new ServiceProcessException(com.utsoft.blockchain.api.util.Constants.EXECUTE_PROCESS_ERROR,"instantiate chaincode {"+chaincodeID+"} and Organization {" + orgconfig + "} objects={"+objects+"} ", e);
		}
	}
	
	  /**
	   * 初始化chanel
	   * @param chaincodeID
	   * @param txPath
	   * @return
	   */
	public boolean initNewChannl(ChaincodeID chaincodeID,File txPath) {
		FabricAuthorizedOrg orgconfig = orgsConfigMap.getOrgByMatch(chaincodeID);
		if (orgconfig==null)
			 throw new ServiceProcessException("init NewChannle or fabricAuthorizedOrg not extis "+chaincodeID);
		 
		  Channel channel = channelClientProxy.initializeNewChannel(client, orgconfig, txPath);
		  if (channel==null) return false;
		  return channel.isInitialized();
	}
}
