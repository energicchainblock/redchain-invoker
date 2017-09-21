package com.utsoft.blockchain.core.rpc.locker;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.core.util.FormatUtil;
import com.utsoft.blockchain.core.util.LocalConstants;
import com.utsoft.blockchain.core.zoo.ZookeeperCallback;
import com.utsoft.blockchain.core.zoo.ZookeeperUtils;
/**
 * 交易排序服务
 * @author hunterfox
 * @date: 2017年9月21日
 * @version 1.0.0
 */
@Component
public class TransactionSequencingService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private ZookeeperUtils zookeeperUtils;

	private ConcurrentLockStrategy lockStrategy;

	@PostConstruct
	public void initStrategy() {
		lockStrategy = new DefaultConcurrentLockStrategy(redisTemplate);
	}

	public BaseResponseModel<String> applyTransactionOrderToken(String address) {
		return transactionOrderToken(address);
	}

	public BaseResponseModel<String> applyTransactionOrderToken(String address, String from) {
		return transactionOrderToken(address, from);
	}

	private BaseResponseModel<String> transactionOrderToken(String... addresss) {

		BaseResponseModel<String> orderIdModel = BaseResponseModel.build();
		for (String address : addresss) {
			if (lockStrategy.isAddressAvailable(address)) {
				return orderIdModel.setCode(Constants.ORDER_APPLY_LOCKER);
			}
		}

		final String lockerPath = addresss[0];
		try {
			return zookeeperUtils.lock(new ZookeeperCallback<BaseResponseModel<String>>() {

				@Override
				public BaseResponseModel<String> callback() throws Exception {

					for (String address : addresss) {
						if (lockStrategy.isAddressAvailable(address)) {
							return orderIdModel.setCode(Constants.ORDER_APPLY_LOCKER);
						}
					}
					String code;
					if (addresss.length == 1) {
						code = lockStrategy.applyTransactionToken(addresss[0]);
					} else {
						code = lockStrategy.applyTransactionToken(addresss[0], addresss[1]);
					}
					orderIdModel.setData(code);
					return orderIdModel;
				}

				@Override
				public String getLockPath() {
					return "/blockchain/ordering-" + lockerPath;
				}
			});
		} catch (Exception ex) {
			logger.error("transactionOrderToken apply fail" + addresss, ex);
			orderIdModel.setCode(Constants.SEVER_INNER_ERROR);
		}
		return orderIdModel;
	}

	public void releaseLocker(String address) {
		lockStrategy.releaseToken(address);
	}

	public String getRecoderCode(String address) {
		String addressPrefix = FormatUtil.redisPrefix(address, LocalConstants.USER_TRANSACTION_ID);
		return redisTemplate.boundValueOps(addressPrefix).get();
	}

	/**
	 * 牌照是否发放
	 * 
	 * @param address
	 * @param code
	 * @return
	 */
	public boolean isTokenGrant(String address, String code) {

		String addressPrefix = FormatUtil.redisPrefix(address, LocalConstants.USER_TRANSACTION_ID);
		String repositoryCode = redisTemplate.boundValueOps(addressPrefix).get();
		if (code.equals(repositoryCode)) {
			return true;
		}
		return false;
	}
}
