package com.utsoft.blockchain.core.service.impl;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.springframework.stereotype.Service;

import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoDto;
import com.utsoft.blockchain.core.service.AbstractTkcBasicService;
import com.utsoft.blockchain.core.service.ITkcBcRepository;
/**
 *  区块链基本信息查询
 * @author hunterfox
 * @date: 2017年7月31日
 * @version 1.0.0
 */
@Service
public class TkcBcRepositoryImpl extends AbstractTkcBasicService implements ITkcBcRepository {

	
	@Override
	public TkcTransactionBlockInfoDto queryTransactionBlockByID(String applycode, String txtId)
			throws ServiceProcessException {
		TkcTransactionBlockInfoDto tblockInfo = null;
		ChaincodeID chaincodeID = getChainCode(applycode);
		if (chaincodeManager.checkChannelActive(chaincodeID)) {
			tblockInfo = new TkcTransactionBlockInfoDto();
			tblockInfo  = chaincodeManager.querySourceBlockByTransactionID(chaincodeID, txtId);
		} else
		   chaincodeManager.reconnect(chaincodeID);
		 return tblockInfo;
	}
}
