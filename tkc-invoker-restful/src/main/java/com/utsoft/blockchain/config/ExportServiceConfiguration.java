package com.utsoft.blockchain.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utsoft.blockchain.core.util.IGlobals;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
/**
 * @author hunterfox
 * @date: 2017年7月28日
 * @version 1.0.0
 */
@Configuration
public class ExportServiceConfiguration  {


	 @Bean
	 public AnnotationBean motanAnnotationBean() {
	     AnnotationBean motanAnnotationBean = new AnnotationBean();
	     motanAnnotationBean.setPackage("com.utsoft.blockchain.core.rpc.provider");
	     return motanAnnotationBean;
	 }
	 
	 @Bean(name = "tkcExportServer")
	 public ProtocolConfigBean protocolConfig() {
	     ProtocolConfigBean config = new ProtocolConfigBean();
	     config.setDefault(true);
	     config.setName("motan");
	     config.setMaxContentLength(1048576);
	     return config;
	 }

	 @Bean(name = "registryConfig")
	 public RegistryConfigBean registryConfig() {
	     
		 RegistryConfigBean config = new RegistryConfigBean();
	     String  zookeeper_address = IGlobals.getProperty("zookeeper_address");
	     config.setRegProtocol("zookeeper");
	     config.setName("rpc_zookeeper");
	     config.setAddress(zookeeper_address);
	     return config;
	 }

	 @Bean
	 public BasicServiceConfigBean baseServiceConfig() {
	   
		 BasicServiceConfigBean config = new BasicServiceConfigBean();
	     config.setExport("tkcExportServer:8002");
	     config.setRegistry("registryConfig");
	     config.setGroup("blockchainTransaction");
	     config.setHost("192.168.51.129");
	     config.setAccessLog(false);
	     config.setShareChannel(true);
	     config.setModule("motan--rpc");
	     config.setApplication("blockchanin");
	     return config;
	 }
	 
}
