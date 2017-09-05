package com.utsoft.blockchain.core.dao.mapper;
import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.utsoft.blockchain.core.dao.MySqlBaseMapper;
import com.utsoft.blockchain.core.dao.model.ChaincodeOrgConfigPo;
/**
 * 链码上的组织配置
 * @author hunterfox
 * @date: 2017年7月29日
 * @version 1.0.0
 */
@Repository
public interface ChaincodeOrgConfigMapper extends MySqlBaseMapper<ChaincodeOrgConfigPo>{

	
	/**
	 * 获得组织下面链码
	 * @param chainId
	 * @return
	 */
	@Select("select a.* from t_chaincode_org_config a INNER JOIN t_chaincode_org b on b.chain_org_id = a.chain_org_id where b.chainId= #{chainId}")
	@ResultMap("BaseResultMap")
	public List<ChaincodeOrgConfigPo> listNodeConfigAddress(Integer chainId);
}
