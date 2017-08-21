package com.utsoft.blockchain.api.util;
import org.apache.commons.codec.DecoderException;
import  org.apache.commons.codec.binary.Hex;
/**
 * 所有加密的类容
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class SdkUtil {

	private static IdWorker idWorker = new IdWorker(1);
	private SdkUtil (){
	 }
	
	/**
	 * Time format generation sequence
	 * @return number id
	 */
    public static String generateId() {
        return String.valueOf(idWorker.nextId());
    }
    
    public static String toHexString(String source) {
       if (source == null) {
            return null;
        }
       byte [] bytes = source.getBytes();
       return Hex.encodeHexString(bytes);
    } 
    
    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
             return null;
         }
        return Hex.encodeHexString(bytes);
     } 
    
     public static byte[] tofromHexStrig(String source) {
    	 if (source == null) {
            return null;
         }
    	 char [] data = source.toCharArray();
    	 try {
			return Hex.decodeHex(data);
		 } catch (DecoderException e) {
			e.printStackTrace();
		}
    	return null;
     } 
     
     public static boolean isNullOrEmpty(String url) {
         return url == null || url.isEmpty();
     }
}
