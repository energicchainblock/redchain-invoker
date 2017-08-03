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
}
