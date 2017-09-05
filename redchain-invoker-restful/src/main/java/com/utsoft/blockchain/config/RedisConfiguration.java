package com.utsoft.blockchain.config;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.utsoft.blockchain.core.util.LocalConstants;

import redis.clients.jedis.JedisPoolConfig;
/**
 * @author hunterfox
 * @date: 2017年8月7日
 * @version 1.0.0
 */
@Configuration
@EnableCaching
public class RedisConfiguration  extends CachingConfigurerSupport {

	
    @Value("${spring.redis.host}")  
    private String host;  
   
    @Value("${spring.redis.port}")  
    private int port;  
  
    @Value("${spring.redis.password}")  
    private String password;  
   
    @Value("${spring.redis.pool.max-active}")  
    private int maxActive;  
   
    @Value("${spring.redis.pool.max-idle}")  
    private int maxIdle;  
   
    @Value("${spring.redis.pool.min-idle}")  
    private int minIdle;  
   
    @Value("${spring.redis.pool.max-wait}")  
    private int maxWait;  
	    
	 @Bean
	 public KeyGenerator keyGenerator() {
	        return new KeyGenerator() {
	            @Override
	            public Object generate(Object target, Method method, Object... params) {
	                StringBuilder sb = new StringBuilder();
	                sb.append(target.getClass().getName());
	                sb.append(method.getName());
	                for (Object obj : params) {
	                    sb.append(obj.toString());
	                }
	              return sb.toString();
	            }
	       };
	  }
	 
	 @Bean  
	 public JedisConnectionFactory jedisConnectionFactory() {  
	        JedisConnectionFactory factory = new JedisConnectionFactory();  
	        factory.setPassword(password);  
	        factory.setHostName(host);  
	        factory.setPort(port);  
	        factory.setTimeout(60*1000);
	        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();  
	        jedisPoolConfig.setMaxTotal(maxActive);  
	        jedisPoolConfig.setMaxIdle(maxIdle);  
	        jedisPoolConfig.setMinIdle(minIdle);  
	        jedisPoolConfig.setMaxWaitMillis(maxWait);  
	        factory.setPoolConfig(jedisPoolConfig);  
	        return factory;  
	    }  
	 
	    @SuppressWarnings("rawtypes")
	    @Bean
	    public CacheManager cacheManager(RedisTemplate redisTemplate) {
	        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
	       // 设置缓存过期时间
	        rcm.setDefaultExpiration(LocalConstants.REDIS_EXPIRE_TTL);
	        return rcm;
	    }
	    
	    @Bean
	    public RedisTemplate<Serializable, Serializable> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
			RedisTemplate<Serializable, Serializable> redisTemplate = new RedisTemplate<Serializable, Serializable>();
			redisTemplate.setConnectionFactory(redisConnectionFactory);
			return redisTemplate;
		}
	    
	    /*@Bean
	    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory factory) {
	       
	    	StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
	       
	    	RedisSerializer<String> redisSerializer = new StringRedisSerializer();
	    	redisTemplate.setKeySerializer(redisSerializer);  
	    	  
	    	Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);  
	       
	        ObjectMapper om = new ObjectMapper();
	        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
	        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
	        jackson2JsonRedisSerializer.setObjectMapper(om);
	       
	        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
	        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);  
	        
	        redisTemplate.afterPropertiesSet();
	        return redisTemplate;
	    }*/
}
