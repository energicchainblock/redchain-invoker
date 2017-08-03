package com.utsoft.blockchain.config.mybatis;
import java.util.Properties;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.pagehelper.PageInterceptor;
/** 
 * @author <a href="flyskyhunter@gmail.com">王波</a> 
 * @date  2017年7月17日
 * @version 1.0.0
 */
@Configuration
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class DbConfiguration {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	@ConditionalOnMissingBean
	public PageInterceptor pageInterceptor() {

		PageInterceptor interceptor = new PageInterceptor();
		Properties properties = new Properties();
		properties.put("helperDialect", "mysql");
		properties.put("supportMethodsArguments", "true");
		properties.put("reasonable", "true");
		properties.put("params", "count=countSql");
		properties.put("pageSizeZero", "true");
		interceptor.setProperties(properties);
		return interceptor;
	}
	       
}
