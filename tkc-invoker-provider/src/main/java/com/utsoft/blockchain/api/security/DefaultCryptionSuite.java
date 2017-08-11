package com.utsoft.blockchain.api.security;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.exception.WrongfulArgumentException;
import com.utsoft.blockchain.api.util.SdkUtil;
/**
 * 默认套件
 * 
 * @author hunterfox
 * @date: 2017年8月3日
 * @version 1.0.0
 */
public class DefaultCryptionSuite implements FamilySecCrypto {

	private static final Logger logger = LoggerFactory.getLogger(DefaultCryptionSuite.class);

	private final CryptionConfig config = CryptionConfig.getConfig();
	private final String SECURITY_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
	private String CERTIFICATE_FORMAT = config.getCertificateFormat();
	 private int securityLevel = config.getSecurityLevel();
	private String curveName;
	private String hashAlgorithm = config.getHashAlgorithm();
	private  KeyFactory generator;
	   
	 public DefaultCryptionSuite(){
		 Security.addProvider(new BouncyCastleProvider());
		 try {
			generator= KeyFactory.getInstance("ECDSA", SECURITY_PROVIDER);
		} catch (NoSuchAlgorithmException e) {
			logger.error("loader keyfactory is fail:",e);
		} catch (NoSuchProviderException e) {
			logger.error("loader keyfactory is fail:",e);
		}
		 try {
			init();
		} catch (CryptionException | WrongfulArgumentException e) {
			e.printStackTrace();
			logger.error("init hashAlgorithm is fail:",e);
		}
	 }
	 
	public boolean verifySignature(byte[] pemcertificate, String signatureAlgorithm, byte[] signature, byte[] plainText)
			throws CryptionException {
		// TODO Auto-generated method stub
		boolean isVerified;
		if (plainText == null || signature == null || pemcertificate == null) {
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.trace("plaintext in hex: " + DatatypeConverter.printHexBinary(plainText));
			logger.trace("signature in hex: " + DatatypeConverter.printHexBinary(signature));
			logger.trace("PEM cert in hex: " + DatatypeConverter.printHexBinary(pemcertificate));
		}

		try {
			BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(pemcertificate));
			CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_FORMAT);
			X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);

			  //isVerified = validateCertificate(certificate);
			  //if (isVerified) { // only proceed if cert is trusted
				Signature sig = Signature.getInstance(signatureAlgorithm);
				sig.initVerify(certificate);
				sig.update(plainText);
				isVerified = sig.verify(signature);
			//}
		} catch (InvalidKeyException | CertificateException e) {
			CryptionException ex = new CryptionException("Cannot verify signature. Error is: " + e.getMessage()
					+ "\r\nCertificate: " + DatatypeConverter.printHexBinary(pemcertificate), e);
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (NoSuchAlgorithmException | SignatureException e) {
			CryptionException ex = new CryptionException(
					"Cannot verify. Signature algorithm is invalid. Error is: " + e.getMessage(), e);
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
		return isVerified;
	}

	public Certificate arraybytesToCertificate(byte[] certBytes) throws CryptionException {
		if (certBytes == null || certBytes.length == 0) {
			throw new CryptionException("arraybytesToCertificate: input null or zero length");
		}
		X509Certificate certificate;
		try {
			BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(certBytes));
			CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_FORMAT);
			certificate = (X509Certificate) certFactory.generateCertificate(pem);
		} catch (CertificateException e) {
			String emsg = "Unable to converts byte array to certificate. error : " + e.getMessage();
			logger.error(emsg);
			logger.debug("input bytes array :" + new String(certBytes));
			throw new CryptionException(emsg, e);
		}
		return certificate;
	}

	@Override
	public byte[] sign(PrivateKey key, byte[] plainText) throws CryptionException {
		return ecdsaSignToBytes((ECPrivateKey) key, plainText);
	}

	private BigInteger[] preventMalleability(BigInteger[] sigs, BigInteger curveN) {
		BigInteger cmpVal = curveN.divide(BigInteger.valueOf(2L));
		BigInteger sval = sigs[1];
		if (sval.compareTo(cmpVal) == 1) {
			sigs[1] = curveN.subtract(sval);
		}
		return sigs;
	}

	/**
	 * Sign data with the specified elliptic curve private key.
	 *
	 * @param privateKey
	 *            elliptic curve private key.
	 * @param data
	 *            data to sign
	 * @return the signed data.
	 * @throws CryptoException
	 */
	private byte[] ecdsaSignToBytes(ECPrivateKey privateKey, byte[] data) throws CryptionException {
		try {
			final byte[] encoded = hash(data);

			// char[] hexenncoded = Hex.encodeHex(encoded);
			// encoded = new String(hexenncoded).getBytes();
			X9ECParameters params = NISTNamedCurves.getByName(this.curveName);
			BigInteger curveN = params.getN();

			ECDomainParameters ecParams = new ECDomainParameters(params.getCurve(), params.getG(), curveN,
					params.getH());

			ECDSASigner signer = new ECDSASigner();
			ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey.getS(), ecParams);
			signer.init(true, privKey);
			BigInteger[] sigs = signer.generateSignature(encoded);

			sigs = preventMalleability(sigs, curveN);

			ByteArrayOutputStream s = new ByteArrayOutputStream();

			DERSequenceGenerator seq = new DERSequenceGenerator(s);
			seq.addObject(new ASN1Integer(sigs[0]));
			seq.addObject(new ASN1Integer(sigs[1]));
			seq.close();
			return s.toByteArray();

		} catch (Exception e) {
			throw new CryptionException("Could not sign the message by private key", e);
		}
	}

	@Override
	public byte[] hash(byte[] plainText) {
		Digest digest = getHashDigest();
		byte[] retValue = new byte[digest.getDigestSize()];
		digest.update(plainText, 0, plainText.length);
		digest.doFinal(retValue, 0);
		return retValue;
	}

	private Digest getHashDigest() {
		if (this.hashAlgorithm.equalsIgnoreCase("SHA3")) {
			return new SHA3Digest();
		} else {
			// Default to SHA2
			return new SHA256Digest();
		}
	}

	boolean validateCertificate(Certificate cert) {

		boolean isValidated;
		if (cert == null) {
			return false;
		}

		try {
			KeyStore keyStore = getTrustStore();

			PKIXParameters parms = new PKIXParameters(keyStore);
			parms.setRevocationEnabled(false);

			CertPathValidator certValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType()); // PKIX

			ArrayList<Certificate> start = new ArrayList<>();
			start.add(cert);
			CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_FORMAT);
			CertPath certPath = certFactory.generateCertPath(start);

			certValidator.validate(certPath, parms);
			isValidated = true;
		} catch (KeyStoreException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
				| CertificateException | CertPathValidatorException | CryptionException e) {
			Object[] args = { e.getMessage(), cert.toString() };
			logger.error("Cannot validate certificate. Error is:{} Certificate {}", args);
			isValidated = false;
		}
		return isValidated;
	}

	/**
	 * all private store collections
	 */
	private KeyStore trustStore = null;
	private void createTrustStore() throws CryptionException {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);
			setTrustStore(keyStore);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| WrongfulArgumentException e) {
			throw new CryptionException("Cannot create trust store. Error: " + e.getMessage(), e);
		}
	}
	
	private  void setTrustStore(KeyStore keyStore) throws WrongfulArgumentException {
		if (keyStore == null) {
			throw new WrongfulArgumentException("Need to specify a java.security.KeyStore input parameter");
		}
		trustStore = keyStore;
	}

	/**
	 * getTrustStore returns the KeyStore object where we keep trusted
	 * certificates. If no trust store has been set, this method will create
	 * one.
	 *
	 * @return the trust store as a java.security.KeyStore object
	 * @throws CryptionException
	 * @see KeyStore
	 */
	public KeyStore getTrustStore() throws CryptionException {
		if (trustStore == null) {
			createTrustStore();
		}
	    return trustStore;
	}

	@Override
	public String convertPrivatelicKey(PrivateKey privateKey) throws CryptionException {
		  return SdkUtil.toHexString(privateKey.getEncoded());
	}

	@Override
	public PrivateKey loadPrivateKeyByStr(String privateKeyStr) throws CryptionException {
		try {
		  byte[] sourcePrivateKey  = SdkUtil.tofromHexStrig(privateKeyStr);
           EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(sourcePrivateKey);
           PrivateKey privateKey = generator.generatePrivate(privateKeySpec);
         return privateKey;
       } catch (Exception exp) {
          throw new CryptionException("Unable to convert byte[] into PrivateKey", exp);
      }
	}


	@Override
	public void init() throws CryptionException,WrongfulArgumentException {
		this.setSecurityLevel(this.securityLevel);
	    this.setHashAlgorithm(this.hashAlgorithm);
	}
	

    /**
     * Security Level determines the elliptic curve used in key generation
     *
     * @param securityLevel currently 256 or 384
     * @throws InvalidArgumentException
     */
    void setSecurityLevel(int securityLevel) throws WrongfulArgumentException {
        if (securityLevel != 256 && securityLevel != 384) {
            throw new WrongfulArgumentException("Illegal level: " + securityLevel + " must be either 256 or 384");
        }
        // TODO need to get set of supported curves from #fabric-crypto team
        if (this.securityLevel == 256) {
            this.curveName = "P-256";
        } else if (this.securityLevel == 384) {
            this.curveName = "secp384r1";
        }
    }

    void setHashAlgorithm(String algorithm) throws WrongfulArgumentException {
        if (SdkUtil.isNullOrEmpty(algorithm)
                || !(algorithm.equalsIgnoreCase("SHA2") || algorithm.equalsIgnoreCase("SHA3"))) {
            throw new WrongfulArgumentException("Illegal Hash function family: "
                    + this.hashAlgorithm + " - must be either SHA2 or SHA3");
        }
        this.hashAlgorithm = algorithm;
    }
}
