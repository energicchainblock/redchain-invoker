package com.utsoft.blockchain.config;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import com.utsoft.blockchain.core.util.IGlobals;
/**
 * 获取全部属性
 * @author hunterfox
 * @date: 2017年7月29日
 * @version 1.0.0
 */
public class GlobalPropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {	
	   if (event.getEnvironment() instanceof ConfigurableEnvironment) {
	        for (PropertySource<?> propertySource : ((ConfigurableEnvironment) event.getEnvironment()).getPropertySources()) {
	            if (propertySource instanceof EnumerablePropertySource) {
	            	 for (String key : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
	                	IGlobals.getInstance().getProperties().put(key, propertySource.getProperty(key));
	                }
	            }
	        }
	    } 
	}
}
