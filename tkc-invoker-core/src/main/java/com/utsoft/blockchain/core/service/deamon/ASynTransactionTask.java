package com.utsoft.blockchain.core.service.deamon;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.core.dao.mapper.ChaincodeAccessCodeMapper;
import com.utsoft.blockchain.core.dao.mapper.TransactionResultMapper;
import com.utsoft.blockchain.core.dao.model.ChaincodeAccessCodePo;
import com.utsoft.blockchain.core.dao.model.TransactionResultPo;
import com.utsoft.blockchain.core.service.applicant.PushIntermissionClient;
import com.utsoft.blockchain.core.util.CommonUtil;
import tk.mybatis.mapper.entity.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 异步调用用户账号操作
 * 比如充值成功，转账等等
 * @author hunterfox
 * @date: 2017年8月15日
 * @version 1.0.0
 */
@Component
public class ASynTransactionTask implements Runnable{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TransactionResultMapper transactionResultMapper;
	
	@Autowired
	private ChaincodeAccessCodeMapper chaincodeAccessCodeMapper;
	
	@Autowired
	private PushIntermissionClient intermissionClient;
	 
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	/**
	 * 控制任务执行状态
	 */
	private ReadWriteLock myLock = new ReentrantReadWriteLock();
	
	private AtomicBoolean  isRunning = new AtomicBoolean(false);
	 
	@Scheduled(fixedDelay = 5000)
	public void run() {
			
		   myLock.writeLock().lock();
		   isRunning.compareAndSet(false, true);
		   try
		   {
 			    Example example = new Example(TransactionResultPo.class);
			    List<TransactionResultPo> dataList = transactionResultMapper.selectByExample(example);
				if (CommonUtil.isCollectNotEmpty(dataList)) {
					 
					    dataList.stream().forEach(transactionResult -> {	 
							  handlerMsg(transactionResult);
					 });
				 } 
		   } finally {
			   isRunning.compareAndSet(true, false);
			   myLock.writeLock().unlock();  
		   }
	}
	
	/**
	 * 通知启动回调
	 * @param transactionResult
	 */
	public void notify(TransactionResultPo transactionResult) {
		
		transactionResultMapper.insert(transactionResult);
		if (!isRunning.get()) {
			executor.submit(this);
		}	
	}
	
	private void handlerMsg(TransactionResultPo transactionResult) {	
		try {
			if (transactionResult.getCounter() >=3 || transactionResult.getStatus()==200) {
				transactionResultMapper.updateMoveCallBackHisotryResult(transactionResult);
				return;
			}
			String callbackUrl = getAddressUrl(transactionResult);
			if (CommonUtil.isNullOrEmpty(callbackUrl)) {
				transactionResult.setStatus(2);
				transactionResult.setCallbackTime(new Date());
				transactionResultMapper.updateMoveCallBackHisotryResult(transactionResult);
				return;
			}
			intermissionClient.sendPushmsg(callbackUrl, transactionResult);
		 } catch (ServiceProcessException ex) {
			 logger.error("handlermsg call back:{}", transactionResult,ex);
		  } catch (Exception ex) {
			 logger.error("handlermsg call back:{}", transactionResult,ex);
		}
	 }
	/**
	 * 获取地址
	 * @param transactionResult
	 * @return
	 */
	private String getAddressUrl(TransactionResultPo transactionResult) {
		 Example example = new Example(ChaincodeAccessCodePo.class);
    	 example.createCriteria().andEqualTo("applyCode",transactionResult.getApplyCode());
    	 List<ChaincodeAccessCodePo> list = chaincodeAccessCodeMapper.selectByExample(example);
    		
    	 String callbackUrl = null;
    	 if (CommonUtil.isCollectNotEmpty(list)) {
    	     callbackUrl = list.get(0).getUrlAddress();
    	 }
    	 return  callbackUrl;
	}	
}
