package com.utsoft.blockchain.core.dao.mapper;
import org.apache.ibatis.annotations.Update;
import com.utsoft.blockchain.core.dao.MySqlBaseMapper;
import com.utsoft.blockchain.core.dao.model.TransactionResultPo;
/**
 * 区块链服务器回调状态
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
public interface TransactionResultMapper extends MySqlBaseMapper<TransactionResultPo>{

	 
    @Update("update t_chain_transaction set result_status=#{resultStatus},tstatus=#{status},counter=#{counter},callback_time=#{callbackTime}  where txId=#{txId} and toAccount=#{to}")
	int updateCallBackResult(TransactionResultPo result);
    
    /**
     * 迁移已经完成状态回收的数据
     * @param result
     * @return
     */
	 void updateMoveCallBackHisotryResult(TransactionResultPo result);
}
