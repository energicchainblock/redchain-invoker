package com.utsoft.blockchain.core.rpc.provider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.SubmitRspResultDto;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoDto;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoVo;
import com.utsoft.blockchain.api.pojo.TkcTransferModel;
import com.utsoft.blockchain.api.pojo.TransactionBaseModel;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
import com.utsoft.blockchain.api.security.CryptionConfig;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.utsoft.blockchain.core.dao.model.TransactionResultPo;
import com.utsoft.blockchain.core.rpc.AbstractTkcRpcBasicService;
import com.utsoft.blockchain.core.service.deamon.ASynTransactionTask;
import com.utsoft.blockchain.core.service.impl.RedisRepository;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.FormatUtil;
import com.utsoft.blockchain.core.util.LocalConstants;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
/**
 * tkc 交易信息实现
 * 
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
@MotanService(export = "tkcExportServer:8002")
public class TkcTransactionExportService extends AbstractTkcRpcBasicService implements ITkcTransactionExportService {

    @Autowired
	private RedisRepository<String,Object> redisRepository;
    
    @Autowired
    private  RedisTemplate<String, String> stringRedisTemplate;
    
	@Autowired
	private ASynTransactionTask aSynTransactionTask;
	
	@Override
	public BaseResponseModel<TkcSubmitRspVo> tranfer(TkcTransferModel model,String sign) {

		BaseResponseModel<TkcSubmitRspVo> submitRspModel = BaseResponseModel.build();
		String applyCategory = model.getApplyCategory();
		String from = model.getFrom();
		String to = model.getTo();
		String submitJson = model.getSubmitJson();
		String serviceCode = model.getServiceCode();
		String created = model.getCreated();
		String publicKey = model.getPublicKey();
		/**
		 * 输入参数检查
		 */
		if (CommonUtil.isEmpty(applyCategory,from,serviceCode,submitJson,created,sign,publicKey) ){
		    return submitRspModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		
		synchronized(created) {
			
			String userPrefix = FormatUtil.redisTransferPrefix(from,created);
			TkcTransferModel processOrder = (TkcTransferModel) redisRepository.get(userPrefix);
			if (processOrder!=null) {
				submitRspModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return submitRspModel;
			} 
			redisRepository.set(userPrefix,model,120L,TimeUnit.SECONDS);
			//sign=md5(applyCategory=1&created=2&from=3&publicKey=4&serviceCode=5&submitJson=6&to=7)
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(model.getApplyCategory());
			signaturePlayload.addPlayload(created);
			signaturePlayload.addPlayload(from);
			signaturePlayload.addPlayload(publicKey);
			signaturePlayload.addPlayload(serviceCode);
			signaturePlayload.addPlayload(submitJson);
			signaturePlayload.addPlayload(to);
			try {
				if (verfyPlayload(from,publicKey,signaturePlayload, sign)) {

					TkcSubmitRspVo resultModel = new TkcSubmitRspVo();
					SubmitRspResultDto result = transactionService.tranfer(applyCategory,from,to,serviceCode,submitJson);
					if (result == null)
						return submitRspModel.setCode(Constants.SEVER_INNER_ERROR);

					BeanUtils.copyProperties(result, resultModel);
					resultModel.setExternals(model.getExternals());
					submitRspModel.setData(resultModel);
					
					/**
					 * 记录 to 通知回调
					 */
					TransactionResultPo transactionResult =new TransactionResultPo();
					transactionResult.setTo(to);
					transactionResult.setApplyCode(applyCategory);
					transactionResult.setSubmitId(created);
					transactionResult.setTxId(result.getTxId());
					transactionResult.setBlockStatus((byte)(result.isStatus()?1:0));
					transactionResult.setGmtCreate(new Date());
					transactionResult.setForward(LocalConstants.TRANSACTION_INCONMING);
					aSynTransactionTask.notify(transactionResult);
					/**
					 * 记录b 通知回调
					 */
					transactionResult =new TransactionResultPo();
					transactionResult.setTo(from);
					transactionResult.setApplyCode(applyCategory);
					transactionResult.setSubmitId(created);
					transactionResult.setTxId(result.getTxId());
					transactionResult.setBlockStatus((byte)(result.isStatus()?1:0));
					transactionResult.setGmtCreate(new Date());
					transactionResult.setForward(LocalConstants.TRANSACTION_OUTCONMING);
					aSynTransactionTask.notify(transactionResult);
					
				} else {
					submitRspModel.setCode(Constants.SINGATURE_ERROR);
				}
			} catch (Exception ex) {
				submitRspModel.setCode(Constants.SEVER_INNER_ERROR);
				Object[] args = { signaturePlayload, ex };
				logger.error("tranfer signaturePlayload:{} error:{} ", args);
			}
			return submitRspModel;
		}
	}

	@Override
	public BaseResponseModel<TkcQueryDetailRspVo> getAccountDetail(String applyCategory,String publicKey,String from,String cmd,
			String created, String sign) {

		BaseResponseModel<TkcQueryDetailRspVo> queryModel = BaseResponseModel.build();
		if (CommonUtil.isEmpty(applyCategory,from,created,sign,publicKey,cmd) ){
		    return queryModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		
		synchronized(created) {
			
			String userPrefix = FormatUtil.redisPrefix(from,created);
			boolean exists = stringRedisTemplate.hasKey(userPrefix);
			if (exists) {
				queryModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return queryModel;
			} 
			//sign=md5(applyCategory=1&cmd=2&created=3&from=4&publicKey=5)
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(applyCategory);
			signaturePlayload.addPlayload(cmd);
			signaturePlayload.addPlayload(created);
			signaturePlayload.addPlayload(from);
			signaturePlayload.addPlayload(publicKey);
			stringRedisTemplate.boundValueOps(userPrefix).set(signaturePlayload.toString(),120L,TimeUnit.SECONDS);
			
			try {
				if (verfyPlayload(from,publicKey,signaturePlayload, sign)) {

					TkcQueryDetailRspVo result = transactionService.select(applyCategory, from,cmd);
					if (result == null)
						return queryModel.setCode(Constants.ITEM_NOT_FIND);
					queryModel.setData(result);	
				} else {
					queryModel.setCode(Constants.SINGATURE_ERROR);
				}
			} catch (Exception ex) {
				queryModel.setCode(Constants.SEVER_INNER_ERROR);
				Object[] args = { signaturePlayload, ex };
				logger.error("select account  signaturePlayload:{} error :{}", args);
			}
			return queryModel;
		}	
	}

	@Override
	public BaseResponseModel<TkcTransactionBlockInfoVo> listStockChanges(String applyCategory,String publicKey, String from, String txId,
			String created, String sign) {

		BaseResponseModel<TkcTransactionBlockInfoVo> queryModel = BaseResponseModel.build();
		if (CommonUtil.isEmpty(applyCategory,from,created,txId,sign,publicKey)){
		    return queryModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		
		synchronized(created) {
			String userPrefix = FormatUtil.redisPrefix(from,created);
			boolean exists = stringRedisTemplate.hasKey(created);
			if (exists) {
				queryModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return queryModel;
			} 
			//sign=md5(applyCategory=1&created=2&from=3&publicKey=4&txId=5)
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(applyCategory);
			signaturePlayload.addPlayload(created);
			signaturePlayload.addPlayload(from);
			signaturePlayload.addPlayload(publicKey);
			signaturePlayload.addPlayload(txId);
		
			stringRedisTemplate.boundValueOps(userPrefix).set(signaturePlayload.toString(),120L,TimeUnit.SECONDS);
			try {
				if (verfyPlayload(from,publicKey,signaturePlayload, sign)) {

					TkcTransactionBlockInfoDto tkcTransactionBlockInfoDto = tkcBcRepository.queryTransactionBlockByID(applyCategory, txId);

					if (tkcTransactionBlockInfoDto == null)
						return queryModel.setCode(Constants.SEVER_INNER_ERROR);

					TkcTransactionBlockInfoVo toBean = new TkcTransactionBlockInfoVo();
					BeanUtils.copyProperties(tkcTransactionBlockInfoDto, toBean);
					queryModel.setData(toBean);
				} else
					queryModel.setCode(Constants.SINGATURE_ERROR);
			} catch (Exception ex) {
				queryModel.setCode(Constants.SEVER_INNER_ERROR);
				Object[] args = { signaturePlayload, ex };
				logger.error("get block index signaturePlayload:{} error :{}", args);
			}
			return queryModel;
		}
	}

	
	@Override
	public BaseResponseModel<TkcSubmitRspVo> recharge(TransactionBaseModel model, String sign) {
		
		BaseResponseModel<TkcSubmitRspVo> submitRspModel = BaseResponseModel.build();
		String applyCategory = model.getApplyCategory();
		String to = model.getTo();
		String submitJson = model.getSubmitJson();
		String serviceCode = model.getServiceCode();
		String created = model.getCreated();
		String publicKey = model.getPublicKey();
		
		/**
		 * 输入参数检查
		 */
		if (CommonUtil.isEmpty(applyCategory,to,submitJson,publicKey,created,sign) ){
		    return submitRspModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
	
		synchronized(created) {
			
			String userPrefix = FormatUtil.redisRechargePrefix(to,created);
			TkcTransferModel processOrder = (TkcTransferModel) redisRepository.get(userPrefix);
			if (processOrder!=null) {
				submitRspModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return submitRspModel;
			} 
			redisRepository.set(userPrefix,model,120L,TimeUnit.SECONDS);
			
			//sign=md5(applyCategory=1&created=2&publicKey=3&serviceCode=4&submitJson=5&to=6)
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(model.getApplyCategory());
			signaturePlayload.addPlayload(created);
			signaturePlayload.addPlayload(publicKey);
			signaturePlayload.addPlayload(serviceCode);
			signaturePlayload.addPlayload(submitJson);
			signaturePlayload.addPlayload(to);
			
			try {
				if (verfyPlayload(to,publicKey,signaturePlayload, sign)) {

					TkcSubmitRspVo resultModel = new TkcSubmitRspVo();
					SubmitRspResultDto result = transactionService.recharge(applyCategory,to,serviceCode,submitJson);
					if (result == null)
						return submitRspModel.setCode(Constants.SEVER_INNER_ERROR);

					/**
					 * 记录 to 通知回调
					 */
					TransactionResultPo transactionResult =new TransactionResultPo();
					transactionResult.setTo(to);
					transactionResult.setApplyCode(applyCategory);
					transactionResult.setSubmitId(created);
					transactionResult.setTxId(result.getTxId());
					transactionResult.setBlockStatus((byte)(result.isStatus()?1:0));
					transactionResult.setGmtCreate(new Date());
					transactionResult.setForward(LocalConstants.TRANSACTION_INCONMING);
					aSynTransactionTask.notify(transactionResult);
					
					BeanUtils.copyProperties(result, resultModel);
					resultModel.setExternals(model.getExternals());
					submitRspModel.setData(resultModel);
				} else {
					submitRspModel.setCode(Constants.SINGATURE_ERROR);
				}
			} catch (Exception ex) {
				submitRspModel.setCode(Constants.SEVER_INNER_ERROR);
				Object[] args = { signaturePlayload, ex };
				logger.error("tranfer signaturePlayload:{} error:{} ", args);
			}
			return submitRspModel;
		}
	}
	
	@Override
	public List<TkcTransactionBlockInfoVo> listStockChanges(String applyCategory,String... txIds) {
		
		List<TkcTransactionBlockInfoVo> dataList = new ArrayList<>();
		if(CommonUtil.isEmpty(txIds) || CommonUtil.isEmpty(applyCategory)) {
			return dataList;
		}
		for (String txId: txIds) {
			TkcTransactionBlockInfoVo tkcBlockInfo  = (TkcTransactionBlockInfoVo)redisRepository.get(txId);
			if (tkcBlockInfo==null) {
				 tkcBlockInfo = (TkcTransactionBlockInfoVo)tkcBcRepository.queryTransactionBlockByID(applyCategory, txId);	
				if (tkcBlockInfo!=null)
				 redisRepository.set(txId, tkcBlockInfo);
			 }
			if (tkcBlockInfo!=null)
		     dataList.add(tkcBlockInfo);
		}
		return dataList;
	}
	
	
	@Override
	public BaseResponseModel<TkcQueryDetailRspVo> getSystemDetail(String applyCategory, String cmd, String created) {
		 BaseResponseModel<TkcQueryDetailRspVo> queryModel = BaseResponseModel.build();
		 if (CommonUtil.isEmpty(applyCategory,created,cmd) ){
		    return queryModel.setCode(Constants.PARAMETER_ERROR_NULl);
		 }
		  synchronized(created) {
			
			String userPrefix = FormatUtil.redisPrefix(created);
			boolean exists = stringRedisTemplate.hasKey(userPrefix);
			if (exists) {
				queryModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return queryModel;
			 } 
			try{
			
			TkcQueryDetailRspVo result = transactionService.select(applyCategory,cmd);
			 if (result == null)
				return queryModel.setCode(Constants.ITEM_NOT_FIND);
			 queryModel.setData(result);	
				
			} catch (Exception ex) {
				queryModel.setCode(Constants.SEVER_INNER_ERROR);
				Object[] args = {applyCategory , cmd, ex };
				logger.error("select system applycode:{} cmd {} error :{}", args);
			}
		   return queryModel;
		}
	}
	
	
	/**
	 * 签名验证
	 * 
	 * @param from
	 * @param signaturePlayload
	 * @param sourceSign
	 * @return
	 */
	private boolean verfyPlayload(String from,String publicKey,SignaturePlayload signaturePlayload, String sourceSign) {
		
		byte[] plainText = signaturePlayload.originalPacket();
		byte[] signature = SdkUtil.decodeHexStrig(sourceSign);
		byte[] certificate = SdkUtil.decodeHexStrig(publicKey);
		CryptionConfig config = CryptionConfig.getConfig();
		try {
			return familySecCrypto.verifySignatureByPublic(certificate, config.getSignatureAlgorithm(), signature, plainText);
		} catch (CryptionException e) {
			e.printStackTrace();
		}
		return false;
	}
}
