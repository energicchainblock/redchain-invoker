package com.utsoft.blockchain.lib.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.utsoft.blockchain.api.proivder.ITkcAccountStoreExportService;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
import com.weibo.api.motan.config.BasicRefererInterfaceConfig;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.RegistryConfig;
/**
 * @author hunterfox
 * @date: 2017年8月1日
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)

public class ImportPrcClientConfig {

	@Autowired
	private ZookeeperProperties zookeeperProperties;

	@Bean(name = "registry")
	public RegistryConfig registryConfig() {

		RegistryConfig config = new RegistryConfig();
		config.setId("registry");
		config.setRegProtocol("zookeeper");
		config.setRegistrySessionTimeout(60*1000);
		config.setConnectTimeout(30*1000);
		String zookeeper_address = zookeeperProperties.getServers();
		config.setAddress(zookeeper_address);
		return config;
	}

	@Bean(name = "protocalConfig")
	public ProtocolConfig protocolConfig() {

		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName("motan");
		protocol.setDefault(true);
		protocol.setName("motan");
		protocol.setMaxContentLength(1048576);
		return protocol;
	}

	@Bean(name = "motantestClientBasicConfig")
	public BasicRefererInterfaceConfig baseRefererConfig(RegistryConfig registry) {

		BasicRefererInterfaceConfig config = new BasicRefererInterfaceConfig();
		config.setGroup("blockchainTransaction");
		config.setModule("motan-demo-rpc");
		config.setApplication("blockchanin");
		config.setRetries(2);
		config.setThrowException(true);
		return config;
	}

	@Bean("tkcTransactionExportService")
	public ITkcTransactionExportService instanceTransactionExportService(RegistryConfig registry,
			BasicRefererInterfaceConfig basicReferer, ProtocolConfig prod) {

		RefererConfig<ITkcTransactionExportService> motanDemoServiceReferer = new RefererConfig<ITkcTransactionExportService>();
		motanDemoServiceReferer.setInterface(ITkcTransactionExportService.class);
		motanDemoServiceReferer.setGroup("blockchainTransaction");
		motanDemoServiceReferer.setVersion("1.0");
		motanDemoServiceReferer.setId("tkcTransactionExportService");
		motanDemoServiceReferer.setRequestTimeout(300);
		motanDemoServiceReferer.setRegistry(registry);
		motanDemoServiceReferer.setBasicReferer(basicReferer);
		motanDemoServiceReferer.setProtocol(prod);
		return motanDemoServiceReferer.getRef();
	}


	@ConditionalOnBean({BasicRefererInterfaceConfig.class,RegistryConfig.class,ProtocolConfig.class})
	@Bean("tkcAccountStoreExportService")
	public ITkcAccountStoreExportService instanceTkcAccountStoreExportService(RegistryConfig registry,
			BasicRefererInterfaceConfig basicReferer, ProtocolConfig prod) {

		RefererConfig<ITkcAccountStoreExportService> motanDemoServiceReferer = new RefererConfig<ITkcAccountStoreExportService>();
		motanDemoServiceReferer.setInterface(ITkcAccountStoreExportService.class);
		motanDemoServiceReferer.setGroup("blockchainTransaction");
		motanDemoServiceReferer.setVersion("1.0");
		motanDemoServiceReferer.setId("tkcAccountStoreExportService");
		motanDemoServiceReferer.setRequestTimeout(300);
		motanDemoServiceReferer.setRegistry(registry);
		motanDemoServiceReferer.setBasicReferer(basicReferer);
		motanDemoServiceReferer.setProtocol(prod);
		return motanDemoServiceReferer.getRef();
	}
}
