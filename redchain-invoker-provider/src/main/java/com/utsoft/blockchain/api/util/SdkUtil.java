package com.utsoft.blockchain.api.util;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import com.google.common.io.BaseEncoding;
import com.utsoft.blockchain.api.exception.BcAddressFormatException;
/**
 * 所有加密的类容
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class SdkUtil {

	private static IdWorker idWorker = new IdWorker(1);
	
	   /**
     * Hex encoding used throughout the framework. Use with HEX.encode(byte[]) or HEX.decode(CharSequence).
     */
     public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();
    
	 private SdkUtil (){
	 }
	
	/**
	 * Time format generation sequence
	 * @return number id
	 */
    public static String generateId() {
        return String.valueOf(idWorker.nextId());
    }
    
    public static String encodeHexString(String source) {
       if (source == null) {
            return null;
        }
       byte [] bytes = source.getBytes();
       return SuiteBase58.encode(bytes);
    } 
    
    public static String encodeHexString(byte[] bytes) {
         if (bytes == null) {
             return null;
         }
        return SuiteBase58.encode(bytes);
     } 
    
     public static byte[] decodeHexStrig(String source) {
    	 if (source == null) {
            return null;
         }
    	 try {
    		 return SuiteBase58.decode(source);
		  } catch (BcAddressFormatException e) {
			 e.printStackTrace();
		}
    	return null;
     } 
     
     public static boolean isNullOrEmpty(String url) {
         return url == null || url.isEmpty();
     }
     
     /**
      * Calculates RIPEMD160(SHA256(input)). This is used in Address calculations.
      */
     public static byte[] sha256hash160(byte[] input) {
         byte[] sha256 = Sha256Hash.hash(input);
         RIPEMD160Digest digest = new RIPEMD160Digest();
         digest.update(sha256, 0, sha256.length);
         byte[] out = new byte[20];
         digest.doFinal(out, 0);
         return out;
     }
     
     /**
      * Returns a copy of the given byte array in reverse order.
      */
     public static byte[] reverseBytes(byte[] bytes) {
         // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
         // performance issue the matter can be revisited.
         byte[] buf = new byte[bytes.length];
         for (int i = 0; i < bytes.length; i++)
             buf[i] = bytes[bytes.length - 1 - i];
         return buf;
     }
     
     private static int isAndroid = -1;
     public static boolean isAndroidRuntime() {
         if (isAndroid == -1) {
             final String runtime = System.getProperty("java.runtime.name");
             isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
         }
         return isAndroid == 1;
     }
     
     public static long currentTimeMillis() {
         return  System.currentTimeMillis();
     }
     public static long currentTimeSeconds() {
         return currentTimeMillis() / 1000;
     }
}
