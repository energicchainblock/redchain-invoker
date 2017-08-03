package com.utsoft.blockchain.core.fabric;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.hyperledger.fabric.sdk.ChaincodeID;

import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedOrg;

/**
 * 全局 fabric 链接服务器端配置管理
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
public class GobalFabricMapStore {

	private static GobalFabricMapStore gobalFabricStore = new GobalFabricMapStore();
	
	private ConcurrentHashMap<ChaincodeID,FabricAuthorizedOrg>  chaincodeorgMap = new ConcurrentHashMap<>();
	
	private GobalFabricMapStore(){

	}
	
	public static  GobalFabricMapStore getInstance(){
		return gobalFabricStore;
	}
	
	/**
	 * 通过链码获取配置信息
	 * @param chaincodeId
	 */
	public FabricAuthorizedOrg getOrgConfigByccId(ChaincodeID chaincodeId) {
		return  chaincodeorgMap.get(chaincodeId);
	}
	
	public void replaceChainOrgConfig(ChaincodeID chaincodeId,FabricAuthorizedOrg orgConfig ){
		chaincodeorgMap.replace(chaincodeId, orgConfig);
	}
	
	public void putChainOrgConfig(ChaincodeID chaincodeId,FabricAuthorizedOrg orgConfig ){
		chaincodeorgMap.put(chaincodeId, orgConfig);
	}
	
	public final Collection<FabricAuthorizedOrg> getCollections() {
		return chaincodeorgMap.values();
	}
	
}
