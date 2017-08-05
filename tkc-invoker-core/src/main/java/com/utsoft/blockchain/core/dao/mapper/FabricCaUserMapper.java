package com.utsoft.blockchain.core.dao.mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.utsoft.blockchain.core.dao.MySqlBaseMapper;
import com.utsoft.blockchain.core.dao.model.FabricCaUserPo;
/**
 * fabric ca user mapper
 * @author hunterfox
 * @date: 2017年8月2日
 * @version 1.0.0
 */
@Repository
public interface FabricCaUserMapper extends MySqlBaseMapper<FabricCaUserPo> {

	  @Update("update t_fabric_ca_user set status= 0 where user_name=#{username} ")
	  int updateFabricUserStatus(@Param("username") String username);
}
