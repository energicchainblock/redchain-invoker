package com.utsoft.blockchain.core.zoo;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZookeeperUtils {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CuratorFramework client;
	
	 public <T> T lock(ZookeeperCallback<T> callback) throws Exception{
		InterProcessMutex lock = new InterProcessMutex(client, callback.getLockPath());
		try{
			if (lock.acquire(10, TimeUnit.SECONDS)) {
				return callback.callback();
			}
		} finally {
			if(lock!=null&&lock.isAcquiredInThisProcess()){
				lock.release();
			}
		}
		return null;
	}
}

