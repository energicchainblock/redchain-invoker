package com.utsoft.blockchain;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * 
 * @author hunterfox
 * @date: 2017年8月7日
 * @version 1.0.0
 */
@Profile({"test"}) 
@ContextConfiguration(classes = { TkcInvokerApplicationServer.class })
@RunWith(SpringRunner.class)
@SpringBootTest()
public class RedisSampleTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void test() throws Exception {
       
    	  stringRedisTemplate.opsForValue().set("key1", "111");
    	  String result = stringRedisTemplate.opsForValue().get("key1");
          assertEquals("111",result );
          
          redisTemplate.opsForValue().set("key2","222");
          String  test2 = redisTemplate.opsForValue().get("key2");
          assertEquals("222",test2 );    
	 }
}
