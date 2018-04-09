package com.hf.core.dao.local;

import com.hf.core.model.po.PayRequestBack;
import org.apache.ibatis.annotations.Param;

public interface PayRequestBackDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayRequestBack record);

    int insertSelective(PayRequestBack record);

    PayRequestBack selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayRequestBack record);

    int updateByPrimaryKey(PayRequestBack record);

    PayRequestBack selectByTradeNo(@Param("outTradeNo")String outTradeNo);
}