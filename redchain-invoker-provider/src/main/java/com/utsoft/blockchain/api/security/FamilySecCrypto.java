package com.utsoft.blockchain.api.security;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.ECPublicKey;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.exception.WrongfulArgumentException;
import com.utsoft.blockchain.api.security.bc.RbcAddress;
/** 
 * 加解密套装
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public interface FamilySecCrypto {
	
	
    /**
     * implementation specific initialization. Whoever constructs a CryptoSuite instance <b>MUST</b> call
     * init before using the instance
     *
     * @throws CryptionException
     * @throws WrongfulArgumentException
     */
    void init() throws CryptionException, WrongfulArgumentException;
    
	 /**
     * Sign the specified byte string.
     *
     * @param key    the {@link java.security.PrivateKey} to be used for signing
     * @param plainText the byte string to sign
     * @return the signed data.
     * @throws CryptionException
     */
    byte[] sign(PrivateKey key, byte[] plainText) throws CryptionException;
    
    /**
     * converts byte array to certificate
     * @param certBytes
     * @return certificate
     * @throws CryptionException
     */
     Certificate arraybytesToCertificate(byte[] certBytes) throws CryptionException;
    
    /**
     * Verify the specified signature
     * @param certificate the certificate of the signer as the contents of the PEM file
     * @param signatureAlgorithm the algorithm used to create the signature.
     * @param signature   the signature to verify
     * @param plainText   the original text that is to be verified
     * @return {@code true} if the signature is successfully verified; otherwise {@code false}.
     * @throws CryptionException
     */
    boolean verifySignature(byte[] certificate, String signatureAlgorithm, byte[] signature, byte[] plainText) throws CryptionException;
  
    /**
     * Hash the specified text byte data.
     *
     * @param plainText the text to hash
     * @return the hashed data.
     */
    byte[] hash(byte[] plainText);
    
    /**
     *  get key String according to PrivateKey
     * @param key
     * @return the base64 data.
     */
     String convertPrivatelicKey(PrivateKey key) throws CryptionException;
     
     /**
      * get key String according to Public key
      * @param key
      * @return the base64 data.
      */
     String convertPublicKey(String key) throws CryptionException;
     
    
     /**
      * from String convert PrivateKey
      * @param privateKey
      * @return PrivateKey otherwise null;
      * @throws CryptionException
      */
      PrivateKey loadPrivateKeyByStr(String privateKey) throws  CryptionException;   
      
      /**
       * 从证书中获取公钥信息
       * @param certificate
       * @return
       * @throws CryptionException
       */
      String loadPublicKeyByCert(byte[] certificate) throws  CryptionException;   
      
     /**
      * default generator KeyFactory
      * @return KeyFactory
      */
      KeyFactory generatorKeyFactory();
     
     public  class Factory {
         private Factory() {

         }
         public static FamilySecCrypto getCryptoSuite() {
             return new DefaultCryptionSuite();
         }
         public static FamilySecCrypto getCryptoSuite(String type) {
        	 FamilySecCrypto familySecCrypto;
             switch (type) {
             case "DEFAULT":
        
             default:
            	 familySecCrypto = new DefaultCryptionSuite() ;
             }
             return familySecCrypto;
         }
     }
}
