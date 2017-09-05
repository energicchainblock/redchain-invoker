package com.utsoft.blockchain.api.security;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.FixedPointUtil;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.security.bc.LazyECPoint;
import com.utsoft.blockchain.api.security.bc.RbcAddress;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.api.util.SdkUtil;
/**
 * @author hunterfox
 * @date: 2017年8月25日
 * @version 1.0.0
 */
public class BcECKey {

	private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1"); 
	//private static final X9ECParameters CURVE_PARAMS  = NISTNamedCurves.getByName("secp256k1");
	//private static final X9ECParameters CURVE_PARAMS = X962NamedCurves.getByName("prime256v1");
    private  static final ECDomainParameters CURVE;
    public static final BigInteger HALF_CURVE_ORDER;
    static {
        // Tell Bouncy Castle to precompute data that's needed during secp256k1 calculations. Increasing the width
        // number makes calculations faster, but at a cost of extra memory usage and with decreasing returns. 12 was
        // picked after consulting with the BC team.
        FixedPointUtil.precompute(CURVE_PARAMS.getG(), 12);
        CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
        HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);
    
    }
    protected long creationTimeSeconds;
    protected  BigInteger priv; 
    protected  LazyECPoint pub;
    private byte[] pubKeyHash;
    /**
     * 密约管理
     */
    private FamilySecCrypto familySecCrypto;
    public BcECKey(FamilySecCrypto familySecCrypto) {
     
      this.familySecCrypto = familySecCrypto;
      creationTimeSeconds = SdkUtil.currentTimeSeconds();
    }

    /**
     * 私钥提取地址
     * @param privateKeyStr
     * @return
     */
    
    public RbcAddress loadAddressByPrivatekey(String privateKeyStr)  throws CryptionException {
 		try {
 			PrivateKey privateKey = familySecCrypto.loadPrivateKeyByStr(privateKeyStr);
 			if (privateKey instanceof BCECPrivateKey) {
 				  BCECPrivateKey privParams = (BCECPrivateKey) privateKey;
 				  this.priv = privParams.getD();
 				  publicKeyFromPrivate();
 				  return toAddress();
 			}
 		} catch (CryptionException e) {
 			 throw new CryptionException("Unable to convert byte[] into PrivateKey", e);
 		}
 		return null;
 	 }
    
    /**
     * 从公钥提取地址
     * @param publicKey
     * @return
     * @throws CryptionException
     */
    public RbcAddress loadAddressByPublickey(String publicKey) throws CryptionException {
    	 byte[] keyBytes = SdkUtil.decodeHexStrig(publicKey);
    	 X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
         KeyFactory keyFactory = familySecCrypto.generatorKeyFactory();
         ECPublicKey ecPublicKey;
		try {
			ecPublicKey = (ECPublicKey) keyFactory.generatePublic(x509KeySpec);
		 } catch (InvalidKeySpecException e) {
			 throw new CryptionException("Unable to convert byte[] into publicKey", e);
		}  
         BigInteger x = ecPublicKey.getW().getAffineX();  
         BigInteger y = ecPublicKey.getW().getAffineY();    
         return fromPublicOnly(x,y);
     }
    
    /**
     * 证书认证
     * @param cert
     * @return
     * @throws CryptionException
     */
     public RbcAddress loadAddressByCert(String cert) throws CryptionException  {
    	
        byte[] keyBytes = SdkUtil.decodeHexStrig(cert);
    	ECPublicKey ecPublicKey = null;
        try {
            BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(keyBytes));
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);
		   ecPublicKey = (ECPublicKey) (certificate.getPublicKey()); //keyFactory.generatePublic(x509KeySpec);
		 } catch (Exception e) {
			 throw new CryptionException("Unable to convert byte[] into publicKey", e);
		}  
        BigInteger x = ecPublicKey.getW().getAffineX();  
        BigInteger y = ecPublicKey.getW().getAffineY();    
        return fromPublicOnly(x,y);
    }
    
    
    public RbcAddress fromPublicOnly(BigInteger x, BigInteger y) {
	     ECPoint  bpoint = publicKeyFromPoint(x, y);
    	 init(bpoint);
    	 return toAddress();
    }
 
    private  void publicKeyFromPrivate() {
        ECPoint point = publicPointFromPrivate();
        point = getPointWithCompression(point, true);
        init(point);
   }
    
    private RbcAddress toAddress() {
        return new RbcAddress(Constants.BC_ADDRESS_VERSION, getPubKeyHash());
    }
    
    /** Gets the hash160 form of the public key (as seen in addresses). */
    private byte[] getPubKeyHash() {
        if (pubKeyHash == null)
            pubKeyHash = SdkUtil.sha256hash160(this.pub.getEncoded());
        return pubKeyHash;
    }
    
    private void init(ECPoint pub) {
        if (priv != null) {
            checkArgument(priv.bitLength() <= 32 * 8, "private key exceeds 32 bytes: %s bits", priv.bitLength());
            // Try and catch buggy callers or bad key imports, etc. Zero and one are special because these are often
            // used as sentinel values and because scripting languages have a habit of auto-casting true and false to
            // 1 and 0 or vice-versa. Type confusion bugs could therefore result in private keys with these values.
            checkArgument(!priv.equals(BigInteger.ZERO));
            checkArgument(!priv.equals(BigInteger.ONE));
        }
        this.pub = new LazyECPoint(checkNotNull(pub));
    }
    
    private  ECPoint getPointWithCompression(ECPoint point, boolean compressed) {
       /*if (point.isCompressed() == compressed)
         return point;*/
        point = point.normalize();
        BigInteger x = point.getAffineXCoord().toBigInteger();
        BigInteger y = point.getAffineYCoord().toBigInteger();
        return CURVE.getCurve().createPoint(x, y);
     }
    
    private ECPoint publicKeyFromPoint(BigInteger x, BigInteger y ) {
    	return CURVE.getCurve().createPoint(x, y);
     }
    
    private  ECPoint publicPointFromPrivate() {
    
       if (priv.bitLength() > CURVE.getN().bitLength()) {
        	priv = priv.mod(CURVE.getN());
        }
        return new FixedPointCombMultiplier().multiply(CURVE.getG(), priv);
    }  
}
