package com.utsoft.blockchain.core.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utsoft.blockchain.core.dao.mapper.KeystoreMapper;
import com.utsoft.blockchain.core.dao.model.KeyStorePo;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;

/**
 * A local file-based key value store.
 * @author hunterfox
 * @date: 2017年7月31日
 * @version 1.0.0
 */
@Service
public class LocalKeyPrivateStoreService {

    private Log logger = LogFactory.getLog(LocalKeyPrivateStoreService.class);
    
    private final Map<String, FabricAuthorizedUser> members = new HashMap<>();
    
    @Autowired
    private KeystoreMapper keySotreMapper;


    /**
     * Get the value associated with name.
     * @param name
     * @return value associated with the name
     */
    public String getValue(String name) {
    	KeyStorePo keystorePo = keySotreMapper.selectByPrimaryKey(name);
    	return keystorePo != null ? keystorePo.getStore() : null;
    }



    /**
     * Set the value associated with name.
     *
     * @param name  The name of the parameter
     * @param value Value for the parameter
     */
    public void setValue(String name, String value) {
    	KeyStorePo keystorePo = keySotreMapper.selectByPrimaryKey(name);
    	if (keystorePo!=null){
    		keystorePo.setStore(value);
        	keySotreMapper.updateByPrimaryKey(keystorePo);
    	} else {
    		keystorePo = new KeyStorePo();
    		keystorePo.setKeyId(name);
    		keystorePo.setStore(value);
    		keySotreMapper.insert(keystorePo);
    	}
    }

  

    /**
     * Get the user with a given name
     * @param name
     * @param org
     * @return user
     */
    public FabricAuthorizedUser getMember(String name, String org) {

        // Try to get the SampleUser state from the cache
    	FabricAuthorizedUser fabricAuthorizedUser = members.get(FabricAuthorizedUser.toKeyValStoreName(name, org));
        if (null != fabricAuthorizedUser) {
            return fabricAuthorizedUser;
        }

        // Create the SampleUser and try to restore it's state from the key value store (if found).
        fabricAuthorizedUser = new FabricAuthorizedUser(name, org, this);

        return fabricAuthorizedUser;

    }

    /**
     * Get the user with a given name
     * @param name
     * @param org
     * @param mspId
     * @param privateKeyFile
     * @param certificateFile
     * @return user
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public FabricAuthorizedUser getMember(String name, String org, String mspId, File privateKeyFile,
                                File certificateFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        try {
            // Try to get the SampleUser state from the cache
        	FabricAuthorizedUser sampleUser = members.get(FabricAuthorizedUser.toKeyValStoreName(name, org));
            if (null != sampleUser) {
                return sampleUser;
            }

            // Create the SampleUser and try to restore it's state from the key value store (if found).
            sampleUser = new FabricAuthorizedUser(name, org, this);
            sampleUser.setMspId(mspId);

            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");

            PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));

            sampleUser.setEnrollment(new SampleStoreEnrollement(privateKey, certificate));

            sampleUser.saveState();

            return sampleUser;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            throw e;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw e;
        }

    }

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Reader pemReader = new StringReader(new String(data));

        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }

        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);

        return privateKey;
    }

    static final class SampleStoreEnrollement implements Enrollment, Serializable {

        private static final long serialVersionUID = -2784835212445309006L;
        private final PrivateKey privateKey;
        private final String certificate;


        SampleStoreEnrollement(PrivateKey privateKey, String certificate)  {


            this.certificate = certificate;

            this.privateKey =  privateKey;
        }

        @Override
        public PrivateKey getKey() {

            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }

    }

}