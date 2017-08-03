package com.utsoft.blockchain.core.rpc.provider;
import org.springframework.beans.factory.annotation.Autowired;

import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoVo;
import com.utsoft.blockchain.api.pojo.TransactionVarModel;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
import com.utsoft.blockchain.api.security.CryptionConfig;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.utsoft.blockchain.api.util.TransactionCmd;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;
import com.utsoft.blockchain.core.rpc.AbstractTkcRpcBasicService;
import com.utsoft.blockchain.core.service.ICaUserService;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
/**
 * tkc 交易信息实现
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
@MotanService(export = "tkcExportServer:8002")
public class TkcTransactionExportService extends AbstractTkcRpcBasicService implements ITkcTransactionExportService {

	@Autowired
	private ICaUserService caUserService;
	
	@Override
	public BaseResponseModel<TkcSubmitRspVo> tranfer(TransactionVarModel model) {
	
		String applycode = model.getApplyCategory();
		String from = model.getFrom();
		String to = model.getTo();
		String submitJson = model.getSubmitJson();
		TransactionCmd cmd = model.getCmd();
		String created = model.getCreated();
		String sign = model.getSign();
		
		transactionService.tranfer(applycode,from, to, cmd, submitJson, created);
		return null;
	}

	@Override
	public BaseResponseModel<TkcQueryDetailRspVo> getTransactionDetail(String applyCategory, String from,
			String created, String sign) {
		
		 BaseResponseModel<TkcQueryDetailRspVo>  queryModel =   BaseResponseModel.build();
		 SignaturePlayload signaturePlayload = new SignaturePlayload();
		 signaturePlayload.addPlayload(applyCategory);
		 signaturePlayload.addPlayload(from);
		 signaturePlayload.addPlayload(created);

		 if (verfyPlayload(from,signaturePlayload,sign))  {
			 
			  TkcQueryDetailRspVo result = transactionService.select(applyCategory,from, TransactionCmd.QUERY, created);; 
			  queryModel.setData(result);
		  } else {
			  queryModel.setCode(Constants.SINGATURE_ERROR);
		  }
		 return  queryModel;
	}

	@Override
	public BaseResponseModel<TkcTransactionBlockInfoVo> listStockChanges(String applyCategory, String from, String txId,
			String created, String sign) {

		  TkcTransactionBlockInfoVo tkcTransactionBlockInfoVo = new TkcTransactionBlockInfoVo();
		  transactionService.select(applyCategory, from, TransactionCmd.QUERY, created);
		  return  BaseResponseModel.build(tkcTransactionBlockInfoVo);
	}
	
	/**
	 * 签名验证
	 * @param from
	 * @param signaturePlayload
	 * @param sourceSign
	 * @return
	 */
	private boolean verfyPlayload(String from,SignaturePlayload signaturePlayload,String sourceSign) {
		  FabricAuthorizedUser fabricuser = caUserService.getFabricUser("admin");
		  byte [] plainText = signaturePlayload.originalPacket();
		  byte []  signature =  SdkUtil.tofromHexStrig(sourceSign); 
		  byte[] certificate =  fabricuser.getEnrollment().getCert().getBytes();
		  CryptionConfig config = CryptionConfig.getConfig();
		  try {
			 return familySecCrypto.verifySignature(certificate,config.getSignatureAlgorithm(), signature, plainText);
		 }  catch (CryptionException e) {
			e.printStackTrace();
		 }
	     return false;
	}
}
