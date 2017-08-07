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

	 public final static int OK = 200;
	 public final static int ITEM_NOT_FIND = 404;
	 
	 public final static int SEVER_INNER_ERROR = 500;
	 /**
	  * 流程处理错误
	  */
	 public final static int  EXECUTE_PROCESS_ERROR = 501;
	 public final static int BAD_REQUEST = 400;
	 /**
	  * 参数错误
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
	 /**
	  * 时间区域
	  */
	 public final static ZoneId ZONEID = ZoneId.systemDefault();
	 public final static DateTimeFormatter DAREFORMATTER =DateTimeFormatter.ofPattern("yyyy年MM月dd日");	 
}
