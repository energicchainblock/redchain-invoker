package com.utsoft.blockchain.core.rpc.provider;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.SubmitRspResultDto;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoDto;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoVo;
import com.utsoft.blockchain.api.pojo.TransactionBaseModel;
import com.utsoft.blockchain.api.pojo.TransactionVarModel;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
import com.utsoft.blockchain.api.security.CryptionConfig;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.utsoft.blockchain.core.dao.model.TransactionResultPo;
import com.utsoft.blockchain.core.fabric.model.FabricAuthorizedUser;
import com.utsoft.blockchain.core.rpc.AbstractTkcRpcBasicService;
import com.utsoft.blockchain.core.service.ICaUserService;
import com.utsoft.blockchain.core.service.deamon.ASynTransactionTask;
import com.utsoft.blockchain.core.service.impl.RedisRepository;
import com.utsoft.blockchain.core.util.CommonUtil;
import com.utsoft.blockchain.core.util.FormatUtil;
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
	private ICaUserService caUserService;

    @Autowired
	private RedisRepository<String,Object> redisRepository;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
	@Autowired
	private ASynTransactionTask aSynTransactionTask;
	
	@Override
	public BaseResponseModel<TkcSubmitRspVo> tranfer(TransactionVarModel model,String sign) {

		BaseResponseModel<TkcSubmitRspVo> submitRspModel = BaseResponseModel.build();
		String applyCategory = model.getApplyCategory();
		String from = model.getFrom();
		String to = model.getTo();
		String submitJson = model.getSubmitJson();
		String cmd = model.getServiceCode();
		String created = model.getCreated();
		/**
		 * 输入参数检查
		 */
		if (CommonUtil.isEmpty(applyCategory,from,to,submitJson,created,sign) ){
		    return submitRspModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		synchronized(created) {
			
			String userPrefix = FormatUtil.redisTransferPrefix(from,created);
			TransactionVarModel processOrder = (TransactionVarModel) redisRepository.get(userPrefix);
			if (processOrder!=null) {
				submitRspModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return submitRspModel;
			} 
			redisRepository.set(userPrefix,model,120L,TimeUnit.SECONDS);
			
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(model.getApplyCategory());
			signaturePlayload.addPlayload(from);
			signaturePlayload.addPlayload(to);
			signaturePlayload.addPlayload(cmd);
			signaturePlayload.addPlayload(submitJson);
			signaturePlayload.addPlayload(created);
			try {
				if (verfyPlayload(from, signaturePlayload, sign)) {

					TkcSubmitRspVo resultModel = new TkcSubmitRspVo();
					SubmitRspResultDto result = transactionService.tranfer(applyCategory, from, to, cmd, submitJson);
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
	public BaseResponseModel<TkcQueryDetailRspVo> getAccountDetail(String applyCategory, String from,
			String created, String sign) {

		BaseResponseModel<TkcQueryDetailRspVo> queryModel = BaseResponseModel.build();
		if (CommonUtil.isEmpty(applyCategory,from,created,sign) ){
		    return queryModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
	
		synchronized(created) {
			
			String userPrefix = FormatUtil.redisPrefix(from,created);
			boolean exists = stringRedisTemplate.hasKey(userPrefix);
			if (exists) {
				queryModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return queryModel;
			} 
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(applyCategory);
			signaturePlayload.addPlayload(from);
			signaturePlayload.addPlayload(created);
			stringRedisTemplate.boundValueOps(userPrefix).set(signaturePlayload.toString(),120L,TimeUnit.SECONDS);
			
			try {
				if (verfyPlayload(from,signaturePlayload, sign)) {

					TkcQueryDetailRspVo result = transactionService.select(applyCategory, from,Constants.QUERY);
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
	public BaseResponseModel<TkcTransactionBlockInfoVo> listStockChanges(String applyCategory, String from, String txId,
			String created, String sign) {

		BaseResponseModel<TkcTransactionBlockInfoVo> queryModel = BaseResponseModel.build();
		if (CommonUtil.isEmpty(applyCategory,from,created,txId,sign) ){
		    return queryModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		synchronized(created) {
			String userPrefix = FormatUtil.redisPrefix(from,created);
			boolean exists = stringRedisTemplate.hasKey(created);
			if (exists) {
				queryModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return queryModel;
			} 
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(applyCategory);
			signaturePlayload.addPlayload(from);
			signaturePlayload.addPlayload(txId);
			signaturePlayload.addPlayload(created);
			stringRedisTemplate.boundValueOps(userPrefix).set(signaturePlayload.toString(),120L,TimeUnit.SECONDS);
			try {
				if (verfyPlayload(from, signaturePlayload, sign)) {

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
		String cmd = model.getServiceCode();
		String created = model.getCreated();
		/**
		 * 输入参数检查
		 */
		if (CommonUtil.isEmpty(applyCategory,to,submitJson,created,sign) ){
		    return submitRspModel.setCode(Constants.PARAMETER_ERROR_NULl);
		}
		synchronized(created) {
			
			String userPrefix = FormatUtil.redisRechargePrefix(to,created);
			TransactionVarModel processOrder = (TransactionVarModel) redisRepository.get(userPrefix);
			if (processOrder!=null) {
				submitRspModel.setCode(Constants.EXECUTE_PROCESS_ERROR);
				return submitRspModel;
			} 
			redisRepository.set(userPrefix,model,120L,TimeUnit.SECONDS);
			
			SignaturePlayload signaturePlayload = new SignaturePlayload();
			signaturePlayload.addPlayload(model.getApplyCategory());
			signaturePlayload.addPlayload(to);
			signaturePlayload.addPlayload(cmd);
			signaturePlayload.addPlayload(submitJson);
			signaturePlayload.addPlayload(created);
			try {
				if (verfyPlayload(to, signaturePlayload, sign)) {

					TkcSubmitRspVo resultModel = new TkcSubmitRspVo();
					SubmitRspResultDto result = transactionService.recharge(applyCategory,to,cmd, submitJson);
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
	
	/**
	 * 签名验证
	 * 
	 * @param from
	 * @param signaturePlayload
	 * @param sourceSign
	 * @return
	 */
	private boolean verfyPlayload(String from, SignaturePlayload signaturePlayload, String sourceSign) {
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
