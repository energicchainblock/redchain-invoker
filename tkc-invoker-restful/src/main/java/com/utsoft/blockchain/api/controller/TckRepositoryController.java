package com.utsoft.blockchain.api.controller;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utsoft.blockchain.api.AbstractController;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoDto;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoVo;
import com.utsoft.blockchain.core.service.ITkcBcRepository;

import io.swagger.annotations.Api;
/**
 * 区块链交易信息查询
 * @author hunterfox
 * @date 2017年7月18日
 * @version 1.0.0
 */
@RestController
@Api(tags = "tkc hisotry repository", value = "tkc basic info  API")
public class TckRepositoryController extends AbstractController {

	
	@Autowired
	private ITkcBcRepository iTkcBcRepository;
	
	/**
	 * 查询交易block 信息
	 * @param applyCode
	 * @param from
	 * @param created
	 * @param sign
	 * @return
	 */
	@RequestMapping(value = "/listStockChanges", method = RequestMethod.GET)
	public BaseResponseModel<TkcTransactionBlockInfoVo> listStockChanges(@RequestParam(required=true) String applyCode,@RequestParam(required=true) String from,String txId,String sign) {
		TkcTransactionBlockInfoDto tkcTransactionBlockInfoDto = iTkcBcRepository.queryTransactionBlockByID(applyCode, txId);
		TkcTransactionBlockInfoVo  toBean = new TkcTransactionBlockInfoVo();
		BeanUtils.copyProperties(tkcTransactionBlockInfoDto,toBean);
		return BaseResponseModel.build(toBean);
	}
	
}
