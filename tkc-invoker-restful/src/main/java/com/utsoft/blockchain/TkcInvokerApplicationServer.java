package com.utsoft.blockchain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.utsoft.blockchain.config.GlobalPropertiesListener;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;
/**
 * @author hunterfox
 * @date 2017年7月17日
 * @version 1.0.0
 */
@SpringBootApplication
//@EnableEurekaClient
public class TkcInvokerApplicationServer {

	public static void main(String[] args) {
		
		 SpringApplication application = new SpringApplication(TkcInvokerApplicationServer.class);
		 application.addListeners(new GlobalPropertiesListener());
		 application.run(args);
		 MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
	}
}