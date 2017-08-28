package com.utsoft.blockchain.api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.utsoft.blockchain.api.AbstractController;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.pojo.TransactionBaseModel;
import com.utsoft.blockchain.api.pojo.TkcTransferModel;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.core.rpc.provider.TkcTransactionExportService;
import io.swagger.annotations.Api;
/**
 *  交易相关
 * @author hunterfox
 * @date 2017年7月18日
 * @version 1.0.0
 */
@RestController
@Api(tags = "transaction", value = "transaction info API")
public class TransactionController extends AbstractController {

	
	@Autowired
	private TkcTransactionExportService transactionService;
	
	/**
	 * 根据交易id查询交易信息
	 * @param applyCode 交易信息代码
	 * @param from 交易发起者
	 * @param created 10位提交时间戳
	 * @param sign  md5签名结果:sign=md5(applyCode=xxx&from=xxx&created=xxx)
	 * @return
	 */
	@RequestMapping(value = "/get_account_detail", method = RequestMethod.GET)
	public BaseResponseModel<TkcQueryDetailRspVo> getOrderByTrade(@RequestParam(required=true) String applyCode,String publicKey,
			@RequestParam(required=true) String from,@RequestParam(defaultValue="1")String created,String sign) {
		 return transactionService.getAccountDetail(applyCode,publicKey,from, created, sign);
	}
	
	/**
	 * 转账操作
	 * @param applyCode 交易信息代码
	 * @param from  账号
	 * @param to   目标账号
	 * @param submitJson 内容
	 * @param created
	 * @param sign  md5签名结果:sign=md5(applyCategory=1&created=2&from=3&publicKey=4&serviceCode=5&submitJson=6&to=7)
	 * @return SubmitRspVo
	 */
	@RequestMapping(value = "/tranfer", method = RequestMethod.POST)
	public BaseResponseModel<TkcSubmitRspVo> tranfer(@RequestParam(required=true) String applyCode,String publicKey,String serviceCode,String from,String to,String submitJson,String created,String sign) {
	
		TkcTransferModel model = new TkcTransferModel(publicKey,applyCode,serviceCode);
		model.setFrom(from);
		model.setTo(to);
		model.setSubmitJson(submitJson);
		model.setCreated(created);
		return transactionService.tranfer(model,sign);
	}
	
	/**
	 * 充值交易
	 * @param applyCode 交易信息代码
	 * @param to   目标账号
	 * @param submitJson 内容
	 * @param created
	 * @param sign  md5签名结果:sign=md5(applyCategory=1&created=2&publicKey=3&serviceCode=4&submitJson=5&to=6)
	 * @return SubmitRspVo
	 */
	@RequestMapping(value = "/recharge", method = RequestMethod.POST)
	public BaseResponseModel<TkcSubmitRspVo> recharge(@RequestParam(required=true) String applyCode,String publicKey,String to,String serviceCode,String submitJson,String created,String sign) {
	
		TransactionBaseModel model = new TransactionBaseModel(publicKey,applyCode,serviceCode);
		model.setTo(to);
		model.setSubmitJson(submitJson);
		model.setCreated(created);
		return transactionService.recharge(model, sign);
	}
}
