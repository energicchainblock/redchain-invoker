package com.utsoft.blockchain.lib.controller;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.proivder.ITkcAccountStoreExportService;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
/**
 * call rpc demo
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
@RestController
public class ExchangedController {

	@Autowired 
	@Lazy(true)
	ITkcTransactionExportService tkcTransactionExportService;
	
	@Autowired
	private ITkcAccountStoreExportService tkcAccountStoreExportService;

	@PostConstruct
	public void init() {
		System.out.println(tkcTransactionExportService);
	}
	
	/**
	 * 获取账户余额
	 * @param applyCode
	 * @param from
	 * @param created
	 * @param sign sign=md5(applyCategory=1&cmd=2&created=3&from=4&publicKey=5)
	 * @return
	 */
	@RequestMapping(value = "/getAccountInfo", method = RequestMethod.GET)
	public TkcQueryDetailRspVo getOrderByTrade(@RequestParam(required=true) String applyCode,String publicKey,
			@RequestParam(required=true) String from,@RequestParam(defaultValue="1")String created,String cmd,String sign) {
	
		BaseResponseModel<TkcQueryDetailRspVo> baseResponse = tkcTransactionExportService.getAccountDetail(applyCode,publicKey,cmd,from,created,sign);
		return baseResponse.getData();
	}
	
}
