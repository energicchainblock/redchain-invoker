package com.utsoft.blockchain.api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.utsoft.blockchain.api.AbstractController;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoVo;
import com.utsoft.blockchain.core.rpc.provider.TkcTransactionExportService;
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
	private TkcTransactionExportService transactionService;
	
	/**
	 * 查询交易block 信息
	 * @param applyCode
	 * @param from
	 * @param txId
	 * @param created
	 * @param sign
	 * @return
	 */
	@RequestMapping(value = "/listStockChanges", method = RequestMethod.GET)
	public BaseResponseModel<TkcTransactionBlockInfoVo> listStockChanges(@RequestParam(required=true) String applyCode,String publicKey,@RequestParam(required=true) String from,String txId,String created,String sign) {
		return transactionService.listStockChanges(applyCode,publicKey,from,txId,created, sign);
	}
}
