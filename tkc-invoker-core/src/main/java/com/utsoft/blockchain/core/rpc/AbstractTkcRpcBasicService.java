package com.utsoft.blockchain.core.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.core.service.ITkcBcRepository;
import com.utsoft.blockchain.core.service.ITransactionService;
import com.utsoft.blockchain.core.service.LocalKeyPrivateStoreService;

/**
 * rpc 抽象服务
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
public abstract class AbstractTkcRpcBasicService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected FamilySecCrypto familySecCrypto = FamilySecCrypto.Factory.getCryptoSuite();
	
	@Autowired
	protected ITkcBcRepository tkcBcRepository;
	
	@Autowired
	protected ITransactionService transactionService;
	
	@Autowired
	protected LocalKeyPrivateStoreService storeService;
}
