package com.utsoft.blockchain.core.dao.mapper;
import org.springframework.stereotype.Repository;

import com.utsoft.blockchain.core.dao.MySqlBaseMapper;
import com.utsoft.blockchain.core.dao.model.KeyStorePo;
/**
 * 密码存储信息
 * @author hunterfox
 * @date 2017年7月18日
 * @version 1.0.0
 */
@Repository
public interface KeystoreMapper extends MySqlBaseMapper<KeyStorePo> {

}
