package com.utsoft.blockchain.api.util;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
/**
 * 非线程安全
 * generate signature
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class SignaturePlayload {

	private  static final String spliteSymbol = "&";
	private StringBuilder sb = new StringBuilder();
	private FamilySecCrypto crypto;
	private boolean isTrim = false;
	public SignaturePlayload() {
		
	}
	public SignaturePlayload(FamilySecCrypto crypto) {
		this.crypto = crypto;
	}
	
	/**
	 * 重新初始化，供下一个使用
	 */
	public void clean() {
		isTrim = false;
		 if(sb.length()>0)
		 sb.delete(0,sb.length()-1);
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
		if (!isTrim && sb.length()>0) {
		    sb.deleteCharAt(sb.length()-1);	
		    isTrim = true; 
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
		if (!isTrim && sb.length()>0) {
		    sb.deleteCharAt(sb.length()-1);	
		    isTrim = true;
		}
		 PrivateKey privateKey = crypto.loadPrivateKeyByStr(key); 
		 byte[] encoded;
		 try {
			byte[] contents = sb.toString().getBytes(Constants.DEFAULT_CHARSET);
			encoded = crypto.sign(privateKey,contents);
		   } catch (UnsupportedEncodingException e) {
			throw  new CryptionException("sign key is error:"+e);
		 }
		 return SdkUtil.encodeHexString(encoded);
	}
	
	@Override
	public String toString() {
		return "SignaturePlayload [sb=" + sb + "]";
	}
	
}
