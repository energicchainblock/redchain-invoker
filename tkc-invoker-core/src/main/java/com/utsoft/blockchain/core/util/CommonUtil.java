package com.utsoft.blockchain.core.util;

import static java.lang.String.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.springframework.util.ResourceUtils;

/**
 * 简单的工具
 * 
 * @author <a href="flyskyhunter@gmail.com">王波</a>
 * @date 2017年7月27日
 * @version 1.0.0
 */
public class CommonUtil {

	private CommonUtil() {
	}

	public static String getSha1(String data) {
		return DigestUtils.sha1Hex(data);
	}

	public static boolean isNotEmpty(String dbId) {
		if (dbId != null && !"".equals(dbId)) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(String ... ssss) {	
		for (String ss: ssss) {
			if (!isNotEmpty(ss)) return true;
		}
		return false;
	}

	public static boolean isNullOrEmpty(String str) {
		return !isNotEmpty(str);
	}

	public static boolean isObjectNotEmpty(Object obj) {
		return obj != null;
	}

	/**
	 * 集合是否为空
	 * 
	 * @param list
	 * @return
	 */
	public static <T> boolean isCollectNotEmpty(Collection<T> list) {
		return list != null && !list.isEmpty();
	}

	/**
	 * 为空
	 * 
	 * @param list
	 * @return
	 */
	public static <T> boolean isCollectEmpty(Collection<T> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * tls 替换
	 * 
	 * @param location
	 * @return
	 */
	public static String grpcTLSify(String location) {

		location = location.trim();
		Exception e = Utils.checkGrpcUrl(location);
		if (e != null) {
			throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
		}
		return IGlobals.getBooleanProperty("connect_fabricTLS", false) ? location.replaceFirst("^grpc://", "grpcs://")
				: location;
	}

	/**
	 * https 替换
	 * 
	 * @param location
	 * @return
	 **/
	public static String httpTLSify(String location) {

		location = location.trim();
		return IGlobals.getBooleanProperty("runningFabricCATLS", false) ? location.replaceFirst("^http://", "https://")
				: location;
	}
	
   public static File findFileSk(File directory) {

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }
   
   /**
    *  peer support tls and cert configuration
    * @param name
    * @return
    */
   public  static Properties getPeerProperties(String name) {
       return getEndPointProperties("peer", name);
   }

   public static Properties getOrdererProperties(String name) {
       return getEndPointProperties("orderer", name);
   }

   public static Properties getEventHubProperties(String name) {
      return getEndPointProperties("peer", name); //uses same as named peer
   }
   
   private  static String getDomainName(final String name) {
       int dot = name.indexOf(".");
       if (-1 == dot) {
           return null;
       } else {
           return name.substring(dot + 1);
       }
   }
   
   public static Properties getEndPointProperties(final String type, final String name) {

         final String orderRootPath = IGlobals.getProperty("fabric.order_root_path");
         final String domainName = getDomainName(name);
		 String fullpath =  Paths.get(orderRootPath.replace("orderer", type), domainName, type + "s",
			               name, "tls/server.crt").toString();
        File cert =null;
		try {
			cert = ResourceUtils.getFile("classpath:"+fullpath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
        if (cert==null || !cert.exists()) {
             throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
                   cert.getAbsolutePath()));
          }
        //  ret.setProperty("trustServerCertificate", "true"); 
        //testing environment only NOT FOR PRODUCTION!
       Properties ret = new Properties();
       ret.setProperty("pemFile", cert.getAbsolutePath());
       ret.setProperty("hostnameOverride", name);
       ret.setProperty("sslProvider", "openSSL");
       ret.setProperty("negotiationType", "TLS");
       return ret;
   }
   
  public static String printableString(final String string) {
       int maxLogStringLength = 64;
       if (string == null || string.length() == 0) {
           return string;
       }

       String ret = string.replaceAll("[^\\p{Print}]", "?");

       ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");

       return ret;

   }
}
