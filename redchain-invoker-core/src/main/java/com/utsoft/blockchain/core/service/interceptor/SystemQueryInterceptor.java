package com.utsoft.blockchain.core.service.interceptor;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * 全局账户金额统计
 * @author hunterfox
 * @date: 2017年9月7日
 * @version 1.0.0
 */
public class SystemQueryInterceptor implements QueryInterceptor {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String  cmd = "total";
	
	private String url;
	private String applycode;

	public SystemQueryInterceptor(String applycode,String url) {
       this.url = url;
       this.applycode = applycode;
	}

	@Override
	public TkcQueryDetailRspVo interceptor(String... req) {
	
		TkcQueryDetailRspVo result = new TkcQueryDetailRspVo();
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder()
		    .url(url)
		    .build();
		
		Call call = okHttpClient.newCall(request);
		try {
		    Response response = call.execute();
		    if (response.isSuccessful()) {
		    	result.setPayload(response.body().string());
		    }
		 } catch (IOException e) {
			logger.error("request is errors:{}", e);
		 }
		return result;
	}

	@Override
	public boolean isAppcodeMatch(String appcode,String cmd) {
		 if (this.applycode!=null)
		   return  applycode.equalsIgnoreCase(appcode) && this.cmd.equalsIgnoreCase(cmd);
		 return false;
	}
}
