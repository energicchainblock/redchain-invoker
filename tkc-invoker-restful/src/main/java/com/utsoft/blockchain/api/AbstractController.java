package com.utsoft.blockchain.api;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.security.CryptionConfig;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;
import com.utsoft.blockchain.core.service.ICaUserService;
/**
 * 基础类
 * @author hunterfox
 * @date 2017年7月17日
 * @version 1.0.0
 */
public abstract class AbstractController {

	protected FamilySecCrypto familySecCrypto = FamilySecCrypto.Factory.getCryptoSuite();
	
	@Resource
	private MessageSource messageSource;

	@Autowired
	private ICaUserService caUserService;
	
	public String formatMsg(String code, Object... args) {
		if (args.length == 0)
			return messageSource.getMessage(code, null, Locale.CHINESE);
		return messageSource.getMessage(code, args, Locale.CHINESE);
	}

	public String formatMsg(HttpServletRequest req, String code, Object... args) {
		if (args.length == 0)
			return messageSource.getMessage(code, null, req.getLocale());
		return messageSource.getMessage(code, args, req.getLocale());
	}
	
	
	protected boolean verfyPlayload(String from, SignaturePlayload signaturePlayload, String sourceSign) {
		FabricAuthorizedUser fabricuser = caUserService.getFabricUser(from);
		byte[] plainText = signaturePlayload.originalPacket();
		byte[] signature = SdkUtil.tofromHexStrig(sourceSign);
		byte[] certificate = fabricuser.getEnrollment().getCert().getBytes();
		CryptionConfig config = CryptionConfig.getConfig();
		try {
			return familySecCrypto.verifySignature(certificate, config.getSignatureAlgorithm(), signature, plainText);
		} catch (CryptionException e) {
			e.printStackTrace();
		}
		return false;
	}
}
