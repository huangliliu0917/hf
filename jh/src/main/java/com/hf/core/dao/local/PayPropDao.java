package com.hf.core.dao.local;

import com.hf.core.model.po.PayProp;

public interface PayPropDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayProp record);

    int insertSelective(PayProp record);

    PayProp selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayProp record);

    int updateByPrimaryKey(PayProp record);
}