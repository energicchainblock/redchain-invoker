package com.utsoft.blockchain.api.util;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
/**
 * generate signature
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class SignaturePlayload {

	private  static final String spliteSymbol = "&";
	private StringBuilder sb = new StringBuilder();
	private FamilySecCrypto crypto;
	
	public SignaturePlayload() {
		
	}
	public SignaturePlayload(FamilySecCrypto crypto) {
		this.crypto = crypto;
	}
	
	/**
	 * Pay attention to the order in order
	 * @param str
	 */
	public void addPlayload(String str) {
		sb.append(str).append(spliteSymbol);
	}
	
	/**
	 * Pay attention to the order in order
	 * @param object
	 */
	public void addPlayload(Object object) {
		sb.append(object).append(spliteSymbol);
	}
	
	/**
	 *  get original package sequence
	 * @return
	 */
	public byte[] originalPacket() {
		if (sb.length()>0) {
		    sb.deleteCharAt(sb.length()-1);	
		}
		try {
			return sb.toString().getBytes(Constants.DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	/**
	 * transport content c message signatures
	 * @param key
	 * @return data of signature
	 * @throws CryptionException
	 */
	public String doSignature(String key) throws CryptionException {
		if (sb.length()>0) {
		    sb.deleteCharAt(sb.length()-1);	
		}
		 PrivateKey privateKey = crypto.loadPrivateKeyByStr(key); 
		 byte[] encoded;
		 try {
			byte[] contents = sb.toString().getBytes(Constants.DEFAULT_CHARSET);
			encoded = crypto.sign(privateKey,contents);
		   } catch (UnsupportedEncodingException e) {
			throw  new CryptionException("sign key is error:"+e);
		 }
		 return SdkUtil.toHexString(encoded);
	}
}
