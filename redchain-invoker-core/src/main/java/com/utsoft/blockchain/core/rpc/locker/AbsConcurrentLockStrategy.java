package com.utsoft.blockchain.core.rpc.locker;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * 默认用户交易锁定抽象类
 * @author hunterfox
 * @date: 2017年9月21日
 * @version 1.0.0
 */
public abstract class AbsConcurrentLockStrategy {

	 protected RedisTemplate<String, String> redisTemplate;
	 public AbsConcurrentLockStrategy(RedisTemplate<String, String> redisTemplate){
		this.redisTemplate = redisTemplate;
	 }
}
