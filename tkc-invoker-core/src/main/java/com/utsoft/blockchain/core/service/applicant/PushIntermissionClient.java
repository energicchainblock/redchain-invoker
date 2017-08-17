package com.utsoft.blockchain.core.service.applicant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.core.dao.mapper.TransactionResultMapper;
import com.utsoft.blockchain.core.dao.model.TransactionResultPo;
import com.utsoft.blockchain.core.pojo.TransactionCallRsp;
/**
 * 回调client 状态跟踪
 * @author hunterfox
 * @date: 2017年8月15日
 * @version 1.0.0
 */
@Component
public class PushIntermissionClient {

	@Autowired
	private RestTemplate template;
   
	@Autowired
	private TransactionResultMapper transactionResultMapper;
	
 
	/**
	 * @param transactionResult
	 * @return
	 * @throws ServiceProcessException
	 */
    public boolean sendPushmsg(String callbackUrl ,TransactionResultPo transactionResult) throws ServiceProcessException {
	  
			Map<String, Object> params = new HashMap<>();
			params.put("reqId", transactionResult.getSubmitId());
			params.put("txId", transactionResult.getTxId());
			params.put("txTime", transactionResult.getCallbackTime()!=null?transactionResult.getCallbackTime().getTime():0L);
			params.put("status", transactionResult.getStatus());
			params.put("user", transactionResult.getTo());
			params.put("forward", transactionResult.getForward());
			try {
				ResponseEntity<TransactionCallRsp> rsp = template.postForEntity(callbackUrl,params,
						TransactionCallRsp.class);
				if (rsp.getStatusCode() == HttpStatus.OK) {

					TransactionCallRsp tcRsp = rsp.getBody();
					transactionResult.setStatus(200);
					transactionResult.setCallbackTime(new Date());
					transactionResult.setCounter((byte) (transactionResult.getCounter() + 1));
					transactionResult.setResultStatus((byte) tcRsp.getStatus());
					transactionResultMapper.updateCallBackResult(transactionResult);
					return true;
				}
				TransactionCallRsp tcRsp = rsp.getBody();
				if (tcRsp != null) {
					transactionResult.setResultStatus((byte) tcRsp.getStatus());
				} else
					transactionResult.setResultStatus((byte) -1);
				transactionResult.setStatus(-1);
				transactionResult.setCallbackTime(new Date());
				transactionResult.setCounter((byte) (transactionResult.getCounter() + 1));
				transactionResultMapper.updateCallBackResult(transactionResult);
				return false;
			} catch (Exception ex) {
			
				transactionResult.setResultStatus((byte) -1);
				transactionResult.setStatus(-1);
				transactionResult.setCallbackTime(new Date());
				transactionResult.setCounter((byte) (transactionResult.getCounter() + 1));
				transactionResultMapper.updateCallBackResult(transactionResult);
				throw new ServiceProcessException(com.utsoft.blockchain.api.util.Constants.EXECUTE_FAIL_ERROR,
						"pushStatus fail", ex);
			}
	}
}

