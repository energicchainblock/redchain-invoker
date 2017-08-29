package com.utsoft.blockchain.api.util;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.CharEncoding;
/**
 * 全局变量
 * @author hunterfox
 * @date 2017年7月17日
 * @version 1.0.0
 */
public class Constants {

	 public final static int BC_ADDRESS_VERSION = 1;
	 public final static int OK = 200;
	 /**
	  * 实体无法找到
	  */
	 public final static int ITEM_NOT_FIND = 404;
	 /**
	  * 实体已经存在
	  */
	 public final static int ITEM_EXITS = 405;
	 /**
	  * 服务器内部处理错误
	  */
	 public final static int SEVER_INNER_ERROR = 500;
	 /**
	  * 流程处理错误
	  */
	 public final static int  EXECUTE_PROCESS_ERROR = 501;
	 /**
	  * 执行失败
	  */
	 public final static int  EXECUTE_FAIL_ERROR = 502;
	 /**
	  * 错误请求
	  */
	 public final static int BAD_REQUEST = 400;
	 /**
	  * 参数错误,不能为空
	  */
	 public final static int PARAMETER_ERROR_NULl = 401;
	 /**
	  * 签名错误
	  */
	 public final static int SINGATURE_ERROR = 402;
	 
	 /**
	  * 并发处理错误，重复提交订单
	  */
	 public final static int CONCURRENT_PROCESS_ERROR = 403;

	 public final static int METHOD_NOT_ALLOW = 405;
	 public final static int MEDIA_NOT_SUPPORT = 415;
	 public final static String PIC_INTERVAL_SYMBOL ="||";
	 
	 public static final long SECOND = 1000;
	 public static final long MINUTE = 60 * SECOND;
	 public static final long HOUR = 60 * MINUTE;
	 public static final long DAY = 24 * HOUR;
	 public static final long WEEK = 7 * DAY;
	 public final static int OFFLINE = 19;
	 public static final int TTL = 5*30;
	 public static final int EVIT_TTL = 2*30;
	 public static final int DEFAULT_ = 2*30;
	
	 public static final String DEFAULT_CHARSET = CharEncoding.UTF_8;
	 
	 public static final String RECHARGE = "recharge";
	 public static final String MOVE = "move";
	 public static final String QUERY = "query";
	 
	 /**
	  * 时间区域
	  */
	 public final static ZoneId ZONEID = ZoneId.systemDefault();
	 public final static DateTimeFormatter DAREFORMATTER =DateTimeFormatter.ofPattern("yyyy年MM月dd日");	 
}
