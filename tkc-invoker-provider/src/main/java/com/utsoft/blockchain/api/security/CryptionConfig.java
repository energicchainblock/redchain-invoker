package com.utsoft.blockchain.api.security;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 密码全局定义
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class CryptionConfig {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String SECURITY_LEVEL = "sdk.security_level";
    public static final String HASH_ALGORITHM = "sdk.hash_algorithm";
    public static final String CACERTS = "sdk.cacerts";
    public static final String ASYMMETRIC_KEY_TYPE = "sdk.crypto.asymmetric_key_type";
    public static final String KEY_AGREEMENT_ALGORITHM = "sdk.crypto.key_agreement_algorithm";
    public static final String SYMMETRIC_KEY_TYPE = "sdk.crypto.symmetric_key_type";
    public static final String SYMMETRIC_KEY_BYTE_COUNT = "sdk.crypto.symmetric_key_byte_count";
    public static final String SYMMETRIC_ALGORITHM = "sdk.crypto.symmetric_algorithm";
    public static final String MAC_KEY_BYTE_COUNT = "sdk.crypto.mac_key_byte_count";
    public static final String CERTIFICATE_FORMAT = "sdk.crypto.certificate_format";
    public static final String SIGNATURE_ALGORITHM = "sdk.crypto.default_signature_algorithm";
    public static final String MAX_LOG_STRING_LENGTH = "sdk.log.stringlengthmax";
 
    private static final Properties sdkProperties = new Properties();
	private static CryptionConfig config = new CryptionConfig();

	private CryptionConfig () {
		  defaultProperty(ASYMMETRIC_KEY_TYPE, "EC");
          defaultProperty(KEY_AGREEMENT_ALGORITHM, "ECDH");
          defaultProperty(SYMMETRIC_KEY_TYPE, "AES");
          defaultProperty(SYMMETRIC_KEY_BYTE_COUNT, "32");
          defaultProperty(SYMMETRIC_ALGORITHM, "AES/CFB/NoPadding");
          defaultProperty(MAC_KEY_BYTE_COUNT, "32");
          defaultProperty(CERTIFICATE_FORMAT, "X.509");
          defaultProperty(SIGNATURE_ALGORITHM, "SHA256withECDSA");
          defaultProperty(SECURITY_LEVEL, "256");
          defaultProperty(HASH_ALGORITHM, "SHA2");
          defaultProperty(MAX_LOG_STRING_LENGTH, "64");
	}
    /**
     * getConfig  for SDK configuration.
     *
     * @return Global configuration
     */
    public static CryptionConfig getConfig() {
        if (null == config) {
            config = new CryptionConfig();
        }
        return config;
    } 
    
    
    /**
     * Get the configured security level. The value determines the elliptic curve used to generate keys.
    *
    * @return the security level.
    */
   public int getSecurityLevel() {

       return Integer.parseInt(getProperty(SECURITY_LEVEL));

   }

   /**
    * Get the name of the configured hash algorithm, used for digital signatures.
    *
    * @return the hash algorithm name.
    */
   public String getHashAlgorithm() {
       return getProperty(HASH_ALGORITHM);

   }

   public String[] getPeerCACerts() {
       return getProperty(CACERTS).split("'");
   }



   public String getAsymmetricKeyType() {
       return getProperty(ASYMMETRIC_KEY_TYPE);
   }

   public String getKeyAgreementAlgorithm() {
       return getProperty(KEY_AGREEMENT_ALGORITHM);
   }

   public String getSymmetricKeyType() {
       return getProperty(SYMMETRIC_KEY_TYPE);
   }

   public int getSymmetricKeyByteCount() {
       return Integer.parseInt(getProperty(SYMMETRIC_KEY_BYTE_COUNT));
   }

   public String getSymmetricAlgorithm() {
       return getProperty(SYMMETRIC_ALGORITHM);
   }

   public int getMACKeyByteCount() {
       return Integer.parseInt(getProperty(MAC_KEY_BYTE_COUNT));
   }

   public String getCertificateFormat() {
       return getProperty(CERTIFICATE_FORMAT);
   }

   public String getSignatureAlgorithm() {
       return getProperty(SIGNATURE_ALGORITHM);
   }

   public int maxLogStringLength() {
       return Integer.parseInt(getProperty(MAX_LOG_STRING_LENGTH));
   }
   
   /**
    * getProperty return back property for the given value.
    *
    * @param property
    * @return String value for the property
    */
   private String getProperty(String property) {
       String ret = sdkProperties.getProperty(property);
       if (null == ret) {
           logger.warn(String.format("No configuration value found for '%s'", property));
       }
       return ret;
   } 
   
   private static void defaultProperty(String key, String value) {
       String ret = System.getProperty(key);
       if (ret != null) {
           sdkProperties.put(key, ret);
       } else {
           String envKey = key.toUpperCase().replaceAll("\\.", "_");
           ret = System.getenv(envKey);
           if (null != ret) {
               sdkProperties.put(key, ret);
           } else {
               if (null == sdkProperties.getProperty(key) && value != null) {
                   sdkProperties.put(key, value);
               }
           }
       }
   }
}
