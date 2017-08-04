package com.utsoft.blockchain.config;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
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
public class GlobalPropertiesListener implements ApplicationListener<ApplicationEvent> {

	/**
	 *  after get cloud configuration, notify ApplicationPreparedEvent
	 * @param event
	 * @return
	 */
	private boolean filterPrepareEvent(ApplicationEvent event)  {
		return ApplicationPreparedEvent.class.isAssignableFrom(event.getClass());
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {	
	 
		if (event instanceof ApplicationEnvironmentPreparedEvent) {
			ApplicationEnvironmentPreparedEvent enviromentEvent = (ApplicationEnvironmentPreparedEvent)event;
			if (enviromentEvent.getEnvironment() instanceof ConfigurableEnvironment) {
		        for (PropertySource<?> propertySource : ((ConfigurableEnvironment) enviromentEvent.getEnvironment()).getPropertySources()) {
		            if (propertySource instanceof EnumerablePropertySource) {
		            	 for (String key : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
		                	IGlobals.getInstance().getProperties().put(key, propertySource.getProperty(key));
		                }
		            }
		        }
		    } 
		} else  if (filterPrepareEvent(event)) {
			 ApplicationPreparedEvent applicationPreparedEvent = (ApplicationPreparedEvent)event;
			 ConfigurableEnvironment environment = applicationPreparedEvent.getApplicationContext().getEnvironment();
			 for (PropertySource<?> propertySource :environment.getPropertySources()) {
		            if (propertySource instanceof EnumerablePropertySource) {
		            	 for (String key : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
		                	 
		            		 if (IGlobals.getInstance().getProperties().get(key)==null)
		            		  IGlobals.getInstance().getProperties().put(key, propertySource.getProperty(key));
		                }
		            }
		        }
		}
	}
}
