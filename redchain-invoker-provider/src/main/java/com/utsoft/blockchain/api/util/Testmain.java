package com.utsoft.blockchain.api.util;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.security.BcECKey;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.api.security.bc.RbcAddress;
/**
 * @author hunterfox
 * @date: 2017年8月28日
 * @version 1.0.0
 */
public class Testmain {
	
	 private static final String SECP256K1 = "secp256k1";
	  public static byte[] getPublicKey(byte[] privateKey) {
		    try {
		      ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(SECP256K1);
		      ECPoint pointQ = spec.getG().multiply(new BigInteger(privateKey));

		      return pointQ.getEncoded(false);
		    } catch (Exception e) {
		      return new byte[0];
		    }
		  }
	  
	   public static RbcAddress loadAddressByPublickey(BcECKey bcECKey,FamilySecCrypto familyCrypto ,byte[] keyBytes ) throws CryptionException {
	    
	    	 X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
	         KeyFactory keyFactory = familyCrypto.generatorKeyFactory();
	         ECPublicKey ecPublicKey;
			try {
				ecPublicKey = (ECPublicKey) keyFactory.generatePublic(x509KeySpec);
			 } catch (InvalidKeySpecException e) {
				 throw new CryptionException("Unable to convert byte[] into publicKey", e);
			}  
	         BigInteger x = ecPublicKey.getW().getAffineX();  
	         BigInteger y = ecPublicKey.getW().getAffineY();    
	         return bcECKey.fromPublicOnly(x,y);
	     }

	public static void main(String[] args) throws InvalidKeySpecException {
	
		FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		BcECKey bcECKey = new BcECKey(familyCrypto);
		try {
		/**	
			String secret= "308193020100301306072a8648ce3d020106082a8648ce3d030107047930770201010420940727510754911bc87fa74c6ba7fff848370060e4535302d67e4eb4bbd703b4a00a06082a8648ce3d030107a14403420004b414540afe14a087e08bcf66521b62d5684c4f865f65dfc5515e9a34629b6a0f88b7d5f4c6c77763f97fd19ce5f18248caaf81a55f9ce4d99d7c99761ee3efbe";
			RbcAddress bbb = bcECKey.loadAddressByPrivatekey(secret);
	     	System.out.println(bbb);
	     	
	     	PrivateKey privateKey = familyCrypto.loadPrivateKeyByStr(secret);
	     	if (privateKey instanceof BCECPrivateKey) {
				  BCECPrivateKey privParams = (BCECPrivateKey) privateKey;
				 // privParams.getEncoded()
				 
			}
	     	
	     	byte[] sourcePrivateKey  = SdkUtil.tofromHexStrig("308193020100301306072a8648ce3d020106082a8648ce3d030107047930770201010420940727510754911bc87fa74c6ba7fff848370060e4535302d67e4eb4bbd703b4a00a06082a8648ce3d030107a14403420004b414540afe14a087e08bcf66521b62d5684c4f865f65dfc5515e9a34629b6a0f88b7d5f4c6c77763f97fd19ce5f18248caaf81a55f9ce4d99d7c99761ee3efbe");
	     	byte [] ss = getPublicKey(sourcePrivateKey);
	     	RbcAddress bbb1 = loadAddressByPublickey(bcECKey,familyCrypto,ss);
	     	
	
	    	//System.out.println(":"+bbb1);
	     	String publicKey = "3059301306072a8648ce3d020106082a8648ce3d030107034200040b11a34708ef7a0ecdd04aa6f8a48a259a33b16cd82f97ead88688130137a981bd3cbe64b39e3f35ce56d81cef77845577ce5781882492a0286e71e8de6e5492";
	     	RbcAddress ress = bcECKey.loadAddressByPublickey(publicKey);
             System.out.println(ress);
               RbcAddress  ddddress = new RbcAddress("caUGaw4vtGKAXLs6ZQW5ey7jvham7BE9U");
             System.out.println(ddddress); */
             
			String ddd= SdkUtil.encodeHexString("wangbo");
			System.out.println(new String(SdkUtil.decodeHexStrig(ddd)));
			
	     	
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}
}
