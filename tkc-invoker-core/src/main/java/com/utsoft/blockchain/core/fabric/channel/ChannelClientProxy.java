package com.utsoft.blockchain.core.fabric.channel;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.hyperledger.fabric.protos.peer.Query.ChaincodeInfo;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.utsoft.blockchain.api.pojo.ReqtOrderDto;
import com.utsoft.blockchain.api.pojo.ReqtQueryOrderDto;
import com.utsoft.blockchain.api.pojo.RspQueryResultDto;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedOrg;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.Constants;
import com.utsoft.blockchain.core.util.FormatUtil;
import com.utsoft.blockchain.core.util.IGlobals;

/**
 * 区块链基本操作逻辑封装
 * @author hunterfox
 * @date: 2017年7月28日
 * @version 1.0.0
 */
public class ChannelClientProxy {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public Channel connectChannel(HFClient client, String name, FabricAuthorizedOrg orgconfig, ChaincodeID chaincodeID)
			throws Exception {

		client.setUserContext(orgconfig.getPeerAdmin());
		Channel newChannel = client.newChannel(name);

		for (String orderName : orgconfig.getOrdererNames()) {
			newChannel.addOrderer(client.newOrderer(orderName, orgconfig.getOrdererLocation(orderName),
					CommonUtil.getOrdererProperties(orderName)));
		}

		for (String peerName : orgconfig.getPeerNames()) {
			String peerLocation = orgconfig.getPeerLocation(peerName);
			Peer peer = client.newPeer(peerName, peerLocation, CommonUtil.getPeerProperties(peerName));

			// Query the actual peer for which channels it belongs to and check
			// it belongs to this channel
			Set<String> channels = client.queryChannels(peer);
			if (!channels.contains(name)) {
				throw new AssertionError(format("Peer %s does not appear to belong to channel %s", peerName, name));
			}

			newChannel.addPeer(peer);
			orgconfig.addPeer(peer);
		}

		for (String eventHubName : orgconfig.getEventHubNames()) {
			EventHub eventHub = client.newEventHub(eventHubName, orgconfig.getEventHubLocation(eventHubName),
					CommonUtil.getEventHubProperties(eventHubName));
			newChannel.addEventHub(eventHub);
		}

		int waitTime = IGlobals.getIntProperty(Constants.PROPOSALWAITTIME, 120000);
		newChannel.setTransactionWaitTime(waitTime);
		newChannel.initialize();

		// Before return lets see if we have the chaincode on the peers that we
		// expect from End2endIT
		// And if they were instantiated too.

		for (Peer peer : newChannel.getPeers()) {

			if (!checkInstalledChaincode(client, peer, chaincodeID.getName(), chaincodeID.getPath(),
					chaincodeID.getVersion())) {
				throw new AssertionError(format("Peer %s is missing chaincode name: %s, path:%s, version: %s",
						peer.getName(), chaincodeID.getName(), chaincodeID.getPath(), chaincodeID.getVersion()));
			}

			if (!checkInstantiatedChaincode(newChannel, peer, chaincodeID.getName(), chaincodeID.getPath(),
					chaincodeID.getVersion())) {

				throw new AssertionError(format(
						"Peer %s is missing instantiated chaincode name: %s, path:%s, version: %s", peer.getName(),
						chaincodeID.getName(), chaincodeID.getPath(), chaincodeID.getVersion()));
			}
		}
		return newChannel;
	}

	/**
	 * peer 检查链码是否安装
	 * 
	 * @param client
	 * @param peer
	 * @param ccName
	 * @param ccPath
	 * @param ccVersion
	 * @return
	 * @throws InvalidArgumentException
	 * @throws ProposalException
	 */
	private boolean checkInstalledChaincode(HFClient client, Peer peer, String ccName, String ccPath, String ccVersion)
			throws InvalidArgumentException, ProposalException {

		if (logger.isInfoEnabled())
			logger.info(FormatUtil.formater("Checking installed chaincode: %s, at version: %s, on peer: %s", ccName,
					ccVersion, peer.getName()));

		List<ChaincodeInfo> ccinfoList = client.queryInstalledChaincodes(peer);
		boolean found = false;
		for (ChaincodeInfo ccifo : ccinfoList) {
			found = ccName.equals(ccifo.getName()) && ccPath.equals(ccifo.getPath())
					&& ccVersion.equals(ccifo.getVersion());
			if (found) {
				break;
			}
		}

		return found;
	}

	/**
	 * peer 检查链码是否安装
	 * 
	 * @param channel
	 * @param peer
	 * @param ccName
	 * @param ccPath
	 * @param ccVersion
	 * @return
	 * @throws InvalidArgumentException
	 * @throws ProposalException
	 */
	private boolean checkInstantiatedChaincode(Channel channel, Peer peer, String ccName, String ccPath,
			String ccVersion) throws InvalidArgumentException, ProposalException {

		if (logger.isInfoEnabled())
			logger.info(FormatUtil.formater("Checking instantiated chaincode: %s, at version: %s, on peer: %s", ccName,
					ccVersion, peer.getName()));

		List<ChaincodeInfo> ccinfoList = channel.queryInstantiatedChaincodes(peer);
		boolean found = false;
		for (ChaincodeInfo ccifo : ccinfoList) {
			found = ccName.equals(ccifo.getName()) && ccPath.equals(ccifo.getPath())
					&& ccVersion.equals(ccifo.getVersion());
			if (found) {
				break;
			}
		}
		return found;
	}

	/**
	 * 主要是事物请求，交易操作
	 * @param chaincodeID
	 * @param order
	 * @throws InvalidArgumentException
	 */
	public CompletableFuture<TransactionEvent> submitRequest(HFClient client, Channel newChannel,
			ChaincodeID chaincodeID, ReqtOrderDto order) throws Exception {

		Collection<ProposalResponse> successful = new LinkedList<>();
		Collection<ProposalResponse> failed = new LinkedList<>();

		int proposalWaitTime = IGlobals.getIntProperty(Constants.PROPOSALWAITTIME, 120000);
		TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
		transactionProposalRequest.setChaincodeID(chaincodeID);
		transactionProposalRequest.setFcn("invoke");
		transactionProposalRequest.setProposalWaitTime(proposalWaitTime);
		transactionProposalRequest.setArgs(
				new String[] { order.getCmd(), order.getFromAccount(), order.getToAccount(), order.getJson() });

		/*
		 * Map<String, byte[]> tm2 = new HashMap<>();
		 * tm2.put("HyperLedgerFabric",
		 * "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
		 * tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
		 * tm2.put("result", ":)".getBytes(UTF_8)); /// This should be returned
		 * see chaincode. transactionProposalRequest.setTransientMap(tm2);
		 */

		Collection<ProposalResponse> transactionPropResp = newChannel
				.sendTransactionProposal(transactionProposalRequest, newChannel.getPeers());
		for (ProposalResponse response : transactionPropResp) {
			if (response.getStatus() == ProposalResponse.Status.SUCCESS) {

				if (logger.isInfoEnabled()) {
					String format = FormatUtil.formater(
							"Successful transaction proposal response Txid: %s from peer %s",
							response.getTransactionID(), response.getPeer().getName());
					logger.info(format);
				}

				successful.add(response);
			} else {
				failed.add(response);
			}
		}

		// Check that all the proposals are consistent with each other. We
		// should have only one set
		// where all the proposals above are consistent.
		Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
				.getProposalConsistencySets(transactionPropResp);
		if (proposalConsistencySets.size() != 1) {
			String msg = FormatUtil.formater("Expected only one set of consistent proposal responses but got %d",
					proposalConsistencySets.size());
			logger.warn(msg);
		}

		if (failed.size() > 0) {
			ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
			logger.info("Not enough endorsers for invoke(move a,b,100):" + failed.size() + " endorser error: "
					+ firstTransactionProposalResponse.getMessage() + ". Was verified: "
					+ firstTransactionProposalResponse.isVerified());
		}

		/*
		 * ProposalResponse resp = transactionPropResp.iterator().next(); byte[]
		 * x = resp.getChaincodeActionResponsePayload(); // This is the data
		 * returned by the chaincode. String resultAsString = null; if (x !=
		 * null) { resultAsString = new String(x, "UTF-8"); } TxReadWriteSetInfo
		 * readWriteSetInfo = resp.getChaincodeActionResponseReadWriteSetInfo();
		 * if (resp.getChaincodeActionResponseStatus()==200) {
		 * 
		 * } int newSetCount = readWriteSetInfo.getNsRwsetCount();
		 */
		// int invokeWaitTime =
		// IGlobals.getIntProperty(Constants.INVOKEWAITTIME, 100000);
		return newChannel.sendTransaction(successful);
	}

	/**
	 * 区块链交易查询
	 * 
	 * @param client
	 * @param channel
	 * @param expect
	 * @param chaincodeID
	 * @param reqtQueryOrderDto
	 * @return
	 */

	public RspQueryResultDto queryChaincode(HFClient client, Channel channel, ChaincodeID chaincodeID,
			ReqtQueryOrderDto reqtQueryOrderDto) {

		RspQueryResultDto rspQueryResultDto = null;
		long consumerTime = System.currentTimeMillis();

		List<String> objects = new ArrayList<String>();
		objects.add(reqtQueryOrderDto.getCmd().toLowerCase());

		if (reqtQueryOrderDto.getToAccount() != null) {
			objects.add(reqtQueryOrderDto.getToAccount());
		}

		if (reqtQueryOrderDto.getJson() != null) {
			objects.add(reqtQueryOrderDto.getJson());
		}

		String[] queryConent = new String[objects.size()];
		objects.toArray(queryConent);

		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(queryConent);
		queryByChaincodeRequest.setFcn("invoke");
		queryByChaincodeRequest.setChaincodeID(chaincodeID);

		Collection<ProposalResponse> queryProposals;
		try {
			queryProposals = channel.queryByChaincode(queryByChaincodeRequest);
		} catch (Exception e) {
			logger.error("Failed during chaincode query with error {} error:{}", objects);
			throw new CompletionException(e);
		}

		List<RspQueryResultDto> results = new ArrayList<>();
		for (ProposalResponse proposalResponse : queryProposals) {

			if (proposalResponse.getStatus() == Status.SUCCESS && proposalResponse.isVerified()) {
				String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
				rspQueryResultDto = new RspQueryResultDto();
				rspQueryResultDto.setPayload(payload);
				rspQueryResultDto.setTimestamp(System.currentTimeMillis() - consumerTime);
				results.add(rspQueryResultDto);

			} else {
				logger.error("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: "
						+ proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage()
						+ ". Was verified : " + proposalResponse.isVerified());
			}
		}
		return CommonUtil.isCollectNotEmpty(results) ? results.get(0) : null;
	}
}
