package com.hf.core.dao.local;

import com.hf.core.model.po.UserFeeRate;

public interface UserFeeRateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserFeeRate record);

    int insertSelective(UserFeeRate record);

    UserFeeRate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserFeeRate record);

    int updateByPrimaryKey(UserFeeRate record);
}