/*package com.utsoft.blockchain.lib;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;

public class DemoRpcClient {

	 public static void main(String[] args) throws InterruptedException {

	        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:client.xml"});

	        ITkcTransactionExportService service = (ITkcTransactionExportService) ctx.getBean("tkcTransactionExportService");
	       
	    	String from = "a";
			String created = "111";
			String sign = "ddddd";
			BaseResponseModel<TkcQueryDetailRspVo> baseResponse = service.getTransactionDetail("sample", from, created, sign);
			System.out.println("result:"+baseResponse);
			
	        System.out.println("motan demo is finish.");
	    }
}
*/