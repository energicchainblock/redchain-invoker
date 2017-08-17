package com.utsoft.blockchain.core.fabric.ca;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RevocationException;
import org.hyperledger.fabric_ca.sdk.helper.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.core.fabric.GobalFabricMapStore;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedOrg;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;
import com.utsoft.blockchain.core.service.LocalKeyPrivateStoreService;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.LocalConstants;
import com.utsoft.blockchain.core.util.IGlobals;
/**
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
public class CaClientManager {

	private static final Logger logger = LoggerFactory.getLogger(CaClientManager.class);

	public static CaClientManager caClientManager = new CaClientManager();
	private GobalFabricMapStore orgsConfigMap = GobalFabricMapStore.getInstance();
	/**
	 * fabric ca client include register,enroll,revoke and others
	 */
	private HFCAClient fabricCaClient;

	private LocalKeyPrivateStoreService localKeyPrivateStoreService;

	private FabricAuthorizedUser admin;

	public static CaClientManager getIntance() {
		return caClientManager;
	}

	/**
	 * keystore 实例化
	 * @param localKeyPrivateStoreService
	 */
	public void serviceInstall(LocalKeyPrivateStoreService localKeyPrivateStoreService) {
		this.localKeyPrivateStoreService =localKeyPrivateStoreService;
	}
	
	/**
	 * 管理员实例化
	 * @param admin
	 */
	public boolean adminInstall(FabricAuthorizedUser admin) throws InstantiationException {
		this.admin =admin;
		if (this.admin.getStatus()==LocalConstants.FABRIC_MANAGER_INVALID) {
			install();
			return true;
		}
		return false;
	}
	
	/**
	 * 安装检查
	 * @throws InstantiationException
	 */
	 private void install () throws  InstantiationException {
			
		if (fabricCaClient==null) {
			Collection<FabricAuthorizedOrg> caList = orgsConfigMap.getCollections();
			if (CommonUtil.isCollectNotEmpty(caList)) {
				FabricAuthorizedOrg firstOrgConfig = caList.stream().findFirst().get();
				try {
					fabricCaClient = HFCAClient.createNewInstance(firstOrgConfig.getCALocation(),
							firstOrgConfig.getCAProperties());
					fabricCaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
				} catch (MalformedURLException e) {
					logger.error("intallCaClient", e);
					throw new InstantiationException("intallCaClient install exception");
				}
			}
		}
		
		if (fabricCaClient!=null && !admin.isEnrolled()) { // Preregistered admin only needs to be enrolled with Fabric CA.
           try {
				admin.setEnrollment(fabricCaClient.enroll(admin.getName(),admin.getEnrollmentSecret()));
				admin.setStatus(LocalConstants.FABRIC_MANAGER_VALID);
			} catch (EnrollmentException | InvalidArgumentException e) {
				throw new InstantiationException("intallCaClient install exception");
			}
       }
	}
	 
	 private void checkFabricClientInstall() {
		if ( fabricCaClient==null){
			intallCaClient();
		}
	}
	private void intallCaClient()   {
		
		if (fabricCaClient==null) {
			Collection<FabricAuthorizedOrg> caList = orgsConfigMap.getCollections();
			if (CommonUtil.isCollectNotEmpty(caList)) {
				FabricAuthorizedOrg firstOrgConfig = caList.stream().findFirst().get();
				try {
					fabricCaClient = HFCAClient.createNewInstance(firstOrgConfig.getCALocation(),
							firstOrgConfig.getCAProperties());
					fabricCaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
				} catch (MalformedURLException e) {
					logger.error("intallCaClient", e);
					throw new ServiceProcessException("intallCaClient install exception");
				}
			}
		}
		
		if (fabricCaClient!=null && !admin.isEnrolled()) { // Preregistered admin only needs to be enrolled with Fabric CA.
            try {
				admin.setEnrollment(fabricCaClient.enroll(admin.getName(),admin.getEnrollmentSecret()));
				admin.setStatus(LocalConstants.FABRIC_MANAGER_VALID);
			} catch (EnrollmentException | InvalidArgumentException e) {
				throw new ServiceProcessException("intallCaClient install exception");
			}
        }
	}

	public FabricAuthorizedUser registerUser(String name, String orgInfo, String affliation,String password) {
		checkFabricClientInstall();
		FabricAuthorizedUser user = new FabricAuthorizedUser(name,orgInfo,LocalConstants.FABRIC_MANAGER_VALID,localKeyPrivateStoreService);
		// users need to be registered AND enrolled
		 RegistrationRequest rr;
		 try {
				rr = new RegistrationRequest(user.getName(), affliation);
		        if (password==null)
				 password = IGlobals.getProperty("ca.password", "tkcQWE123zxc");
				rr.setSecret(password);
				user.setEnrollmentSecret(fabricCaClient.register(rr, admin));
				if (user.getEnrollmentSecret().equals(password)) {
					return user;
				}
			} catch (Exception e) {
				logger.error("registerUser{} error:{}", user,e);
				user = null;
				throw new ServiceProcessException("registerUser fail exception"+e.getMessage());
			}
		 return user;
	}

	public boolean enrollUser(FabricAuthorizedUser user, Map<String, String> properties) {
		checkFabricClientInstall();
		String hostname = IGlobals.getProperty("ca.org.domain", "tangkc.com");
		if (user != null && !user.isEnrolled()) {
			EnrollmentRequest req = new EnrollmentRequest(properties.get("profile"), properties.get("label"), null);
			req.addHost(hostname);
			try {
				user.setEnrollment(fabricCaClient.enroll(user.getName(), user.getEnrollmentSecret(), req));
				// verify
				String cert = user.getEnrollment().getCert();
				return verifyOptions(cert, req);
			} catch (EnrollmentException | InvalidArgumentException | CertificateException e) {
				logger.error("enrollUser", e);
				return false;
			}
		}
		return true;
	}

	public void reenroll(FabricAuthorizedUser user,Map<String, String> properties) {
		checkFabricClientInstall();
		try {
			Enrollment enrollment =fabricCaClient.enroll(user.getName(), user.getEnrollmentSecret());
			user.setEnrollment(enrollment);
		} catch (EnrollmentException | InvalidArgumentException e) {
			e.printStackTrace();
		}
	}

	public void revokeUser(FabricAuthorizedUser user) {
		checkFabricClientInstall();
		try {
			fabricCaClient.revoke(admin,user.getName(), "revoke users");
		} catch (RevocationException | InvalidArgumentException e) {
			logger.error("revoke:",e);
		}
	}
	
	public void revokeEnrollment(FabricAuthorizedUser user) {
		checkFabricClientInstall();
		try {
			fabricCaClient.revoke(user,user.getEnrollment(), "revoke users");
		} catch (RevocationException | InvalidArgumentException e) {
			logger.error("revokeEnrollment:",e);
		}
	}
	
	/**
	 * 验证证书是否合法
	 * 
	 * @param cert
	 * @param req
	 * @return
	 * @throws CertificateException
	 */
	private boolean verifyOptions(String cert, EnrollmentRequest req) throws CertificateException {

		try {
			BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(cert.getBytes()));
			CertificateFactory certFactory = CertificateFactory.getInstance(Config.getConfig().getCertificateFormat());
			X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);

			// check Subject Alternative Names
			Collection<List<?>> altNames = certificate.getSubjectAlternativeNames();
			if (altNames == null) {
				if (req.getHosts() != null && !req.getHosts().isEmpty()) {
					logger.warn("Host name :{} is not included in certificate:{}", cert, req);
				}
				return false;
			}
			ArrayList<String> subAltList = new ArrayList<>();
			for (List<?> item : altNames) {
				int type = ((Integer) item.get(0)).intValue();
				if (type == 2) {
					subAltList.add((String) item.get(1));
				}
			}
			if (!subAltList.equals(req.getHosts())) {
				logger.warn(
						"Subject Alternative Names not matched the host names specified in enrollment request : {} {}",
						cert, req);
				return false;
			}

		} catch (CertificateParsingException e) {
			logger.error("Cannot parse certificate. Error is: " + e.getMessage());
			throw e;
		} catch (CertificateException e) {
			logger.warn("Cannot regenerate x509 certificate. Error is: " + e.getMessage());
			throw e;
		}
		return true;
	}
}
