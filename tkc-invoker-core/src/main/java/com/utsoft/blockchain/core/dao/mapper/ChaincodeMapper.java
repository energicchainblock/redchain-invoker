package com.utsoft.blockchain.core.dao.mapper;
import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.utsoft.blockchain.core.dao.MySqlBaseMapper;
import com.utsoft.blockchain.core.dao.model.ChaincodePo;
/**
 * 链码mapper
 * @author hunterfox
 * @date: 2017年7月28日
 * @version 1.0.0
 */
@Repository
public interface ChaincodeMapper extends MySqlBaseMapper<ChaincodePo> {

	/**
	 * 获取业务关联所有可以获得链码
	 * @return
	 */
	 @Select("select b.* from t_chaincode_caccess_code a INNER JOIN t_chaincode b on a.chainId= b.chainId where a.`status`=1")
     @ResultMap("BaseResultMap")
	public List<ChaincodePo> listCodeAvailable();
}
