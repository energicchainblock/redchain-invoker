package com.utsoft.blockchain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;
/**
 * @author hunterfox
 * @date 2017年7月17日
 * @version 1.0.0
 */
@SpringBootApplication
public class TkcInvokerApplicationServer {

	public static void main(String[] args) {
		
		 SpringApplication application = new SpringApplication(TkcInvokerApplicationServer.class);
		 application.run(args);
		 MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
	}
}