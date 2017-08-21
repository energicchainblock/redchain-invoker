package com.utsoft.blockchain.core.util;
/**
 * 内部使用
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
public class LocalConstants {

	
	 public static final String GOSSIPWAITTIME = "fabric.gossipWaitTime";
	 public static final String INVOKEWAITTIME = "fabric.invokeWaitTime";
	 public static final String DEPLOYWAITTIME = "fabric.deployWaitTime";
	 public static final String PROPOSALWAITTIME = "fabric.proposalWaitTime";
	 
	 public static final int FABRIC_MANAGER_INVALID = 1;
	 public static final int FABRIC_MANAGER_VALID = 0;
	 
	 /**
	  * 转进
	  */
	 public static final byte TRANSACTION_INCONMING = 1;
	 /**
	  * 转出
	  */
	 public static final byte TRANSACTION_OUTCONMING = 0;
	 
	 public static final String NOT_DEBUG_MODE = "fabric.not_debug_mode";
	 /**
	  * 默认过期时间
	  */
	 public static final long REDIS_EXPIRE_TTL = 60*60*24;
	 public static final String TKC_PREFIX ="TKCBC";
	 
	 public static final  String TKC_TRANSFER_MOVE = "TKC_TRANSFER_MOVE";
	 /**
	  * 充值
	  */
	 public static final  String TKC_RECHAHRGE_MOVE = "TKC_RECHAHRGE_MOVE";
}
