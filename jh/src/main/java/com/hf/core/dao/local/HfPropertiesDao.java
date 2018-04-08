package com.hf.core.dao.local;

import com.hf.core.model.po.HfProperties;
import org.apache.ibatis.annotations.Param;

public interface HfPropertiesDao {
    int deleteByPrimaryKey(Long id);

    int insert(HfProperties record);

    int insertSelective(HfProperties record);

    HfProperties selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HfProperties record);

    int updateByPrimaryKey(HfProperties record);

    String selectByKey(@Param("propName") String propName);
}