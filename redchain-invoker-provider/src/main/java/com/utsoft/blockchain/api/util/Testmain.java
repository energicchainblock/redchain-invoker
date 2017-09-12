package com.utsoft.blockchain.api.util;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
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
	  
	   public static RbcAddress loadAddressByPublickey(BcECKey bcECKey,FamilySecCrypto familyCrypto ,byte[] keyBytes ) throws Exception {
	    
	  
	         ECPublicKey ecPublicKey;
	      
	         BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(keyBytes));
	         CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
	         X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);
	         PublicKey publicKey  =  certificate.getPublicKey();
	        
			try {
				ecPublicKey = (ECPublicKey) (publicKey); //keyFactory.generatePublic(x509KeySpec);
			 } catch (Exception e) {
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
			//
			String secret= "6wt5oRhNWbzK6jBnT5HyBQ4goimxLKyXwvh2F4ro1d8T32Ak6JPo9chRLU4bJjMUJXEMZDxn2a2Jypoc5mxGtGstCc2FDaFvZiR5wixjan4EDVQvkrYguzSKYRBRn2iy4x9kyPiuSLSgzks4dA6Ncgar69SpFzWPfL2NE8V5ieHpH454uMx3DzfehpiYNSKyJCUeGEthkZ4EU";
			RbcAddress bbb = bcECKey.loadAddressByPrivatekey(secret);
			RbcAddress bbbd= bcECKey.loadAddressByPublickey("aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTKfAvGSJ6wbZTrogYYqRtJG7KK6mbFmJesJ7TLphVZHamfcGPpiDB4YMWS9oeviSfNTJzfVG2yifhUdVPcgmDjco2");
	     	System.out.println(bbb);
	     	System.out.println(bbbd);


	     	String publicKey = "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTKfAvGSJ6wbZTrogYYqRtJG7KK6mbFmJesJ7TLphVZHamfcGPpiDB4YMWS9oeviSfNTJzfVG2yifhUdVPcgmDjco2";
	     	BcECKey bcECKey1 = new BcECKey(familyCrypto);
	     	RbcAddress ress = bcECKey1.loadAddressByPublickey(publicKey);
            System.out.println(ress);
               

	     	
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}
}
