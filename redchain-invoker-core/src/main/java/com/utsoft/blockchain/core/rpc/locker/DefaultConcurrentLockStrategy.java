package com.utsoft.blockchain.core.rpc.locker;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.core.util.FormatUtil;
import com.utsoft.blockchain.core.util.LocalConstants;
/**
 * 默认地址锁定策略
 * @author hunterfox
 * @date: 2017年9月21日
 * @version 1.0.0
 */
public class DefaultConcurrentLockStrategy extends AbsConcurrentLockStrategy implements ConcurrentLockStrategy {

	public DefaultConcurrentLockStrategy(RedisTemplate<String, String> redisTemplate) {
		super(redisTemplate);
	}

	@Override
	public String applyTransactionToken(String address) throws ServiceProcessException {
		
		String code = SdkUtil.generateId();
	    String addressPrefix = FormatUtil.redisPrefix(address,LocalConstants.USER_TRANSACTION_ID);
		redisTemplate.boundValueOps(addressPrefix).set(code,90L,TimeUnit.SECONDS);
		return code;
	}

	@Override
	public String applyTransactionToken(String address,String from) throws ServiceProcessException{
		
		String code = SdkUtil.generateId();
	    String addressPrefix = FormatUtil.redisPrefix(address,LocalConstants.USER_TRANSACTION_ID);
		redisTemplate.boundValueOps(addressPrefix).set(code,90L,TimeUnit.SECONDS);
	    addressPrefix = FormatUtil.redisPrefix(from,LocalConstants.USER_TRANSACTION_ID);
		redisTemplate.boundValueOps(addressPrefix).set(code,90L,TimeUnit.SECONDS);
		return code;
	}

	@Override
	public boolean isAddressAvailable(String address) {
		String addressPrefix = FormatUtil.redisPrefix(address,LocalConstants.USER_TRANSACTION_ID);
		return redisTemplate.hasKey(addressPrefix);
	}

	@Override
	public void releaseToken(String address) {
		if (isAddressAvailable(address)){
			String addressPrefix = FormatUtil.redisPrefix(address,LocalConstants.USER_TRANSACTION_ID);
			 redisTemplate.delete(addressPrefix);
		}
	}
}
