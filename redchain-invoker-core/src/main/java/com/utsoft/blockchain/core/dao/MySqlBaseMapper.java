package com.utsoft.blockchain.core.dao;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
/**
 * @author hunterfox
 * @date  2017年7月17日
 * @version 1.0.0
 * @param <T>
 */
public interface MySqlBaseMapper <T> extends Mapper<T>, MySqlMapper<T>  {

}
