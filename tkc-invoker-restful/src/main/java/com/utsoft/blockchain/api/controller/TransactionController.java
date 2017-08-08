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
import com.utsoft.blockchain.api.pojo.TransactionVarModel;
import com.utsoft.blockchain.api.util.TransactionCmd;
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
	 * @param sign  md5签名结果:sign=md5(applyCode=1&from=1&created=xxx)
	 * @return
	 */
	@RequestMapping(value = "/get_account_detail", method = RequestMethod.GET)
	public BaseResponseModel<TkcQueryDetailRspVo> getOrderByTrade(@RequestParam(required=true) String applyCode,
			@RequestParam(required=true) String from,@RequestParam(defaultValue="1")String created,String sign) {
		 return transactionService.getTransactionDetail(applyCode, from, created, sign);
	}
	
	/**
	 * 转账操作
	 * @param applyCode 交易信息代码
	 * @param from  账号
	 * @param to   目标账号
	 * @param submitJson 内容
	 * @param created
	 * @param sign  md5签名结果:sign=md5(account_from=1&created=xxx&account_to=xxx)
	 * @return SubmitRspVo
	 */
	@RequestMapping(value = "/tranfer", method = RequestMethod.POST)
	public BaseResponseModel<TkcSubmitRspVo> tranfer(@RequestParam(required=true) String applyCode,String from,String to,String submitJson,String created,String sign) {
	
		TransactionVarModel model = new TransactionVarModel(applyCode,TransactionCmd.MOVE);
		model.setFrom(from);
		model.setTo(to);
		model.setSubmitJson(submitJson);
		model.setCreated(created);
		return transactionService.tranfer(model,sign);
	}
}
