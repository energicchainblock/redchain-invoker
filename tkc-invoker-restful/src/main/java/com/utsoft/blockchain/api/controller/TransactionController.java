package com.utsoft.blockchain.api.controller;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utsoft.blockchain.api.AbstractController;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.SubmitRspResultDto;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.util.TransactionCmd;
import com.utsoft.blockchain.core.service.ITransactionService;

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
	private ITransactionService transactionService;
	
	/**
	 * 根据交易id查询交易信息
	 * @param applyCode 交易信息代码
	 * @param from 交易发起者
	 * @param created 10位提交时间戳
	 * @param sign  md5签名结果:sign=md5(from=1&created=xxx)
	 * @return
	 */
	@RequestMapping(value = "/get_account_detail", method = RequestMethod.GET)
	public BaseResponseModel<TkcQueryDetailRspVo> getOrderByTrade(@RequestParam(required=true) String applyCode,
			@RequestParam(required=true) String from,@RequestParam(defaultValue="1")String created,String sign) {
	
		return BaseResponseModel.build(transactionService.select(applyCode,from,TransactionCmd.QUERY,created));
	}
	
	/**
	 * 转账操作
	 * @param applyCode 交易信息代码
	 * @param account_from  账号
	 * @param account_to   目标账号
	 * @param content 内容
	 * @param created
	 * @param sign  md5签名结果:sign=md5(account_from=1&created=xxx&account_to=xxx)
	 * @return SubmitRspVo
	 */
	@RequestMapping(value = "/tranfer", method = RequestMethod.POST)
	public BaseResponseModel<TkcSubmitRspVo> tranfer(@RequestParam(required=true) String applyCode,String account_from,String account_to,String content,String created,String sign) {
		 TkcSubmitRspVo tkcSubmitRspVo = new TkcSubmitRspVo();
		 SubmitRspResultDto frombean = transactionService.tranfer(applyCode,account_from, account_to,TransactionCmd.MOVE,content,created);
		 BeanUtils.copyProperties(frombean,tkcSubmitRspVo);
		 return BaseResponseModel.build(tkcSubmitRspVo);
	}
}
