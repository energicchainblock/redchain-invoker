package com.utsoft.blockchain.core.util;
import static java.lang.String.format;
/**
 * 
 * @author hunterfox
 * @date: 2017年7月28日
 * @version 1.0.0
 */
public class FormatUtil {

	
	/**
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	  public static String formater(String format, Object... args) {
	      return format(format, args);
	  }
	  
	  /**
	   * 格式化redis prefix 
	   * @param prefix
	   * @param key
	   * @return
	   */
	  public static String redis_cache_prefix(String prefix,String ... keys) {  
		  StringBuilder sb = new StringBuilder ();
		  sb.append(prefix).append("tkc-#$");
		  if (keys!=null)
		    for (String key: keys) {
			  sb.append("@").append(key);
		    }
		  return sb.toString();
	  }
	  
	  public static String redisPrefix(String prefix,String ... key) {
		 return  redis_cache_prefix(prefix,key);
	  }
	  
	  public static String redisTransferPrefix(String ... key) {
		 return redis_cache_prefix(Constants.TKC_TRANSFER_MOVE,key);
	  }  
	  
	  public static String redisRechargePrefix(String ... key) {
		 return redis_cache_prefix(Constants.TKC_RECHAHRGE_MOVE,key);
	}  
}
