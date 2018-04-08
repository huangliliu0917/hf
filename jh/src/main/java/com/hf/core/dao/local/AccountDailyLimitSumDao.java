package com.hf.core.dao.local;

import com.hf.core.model.po.AccountDailyLimitSum;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface AccountDailyLimitSumDao {
    int deleteByPrimaryKey(Long id);

    int insert(AccountDailyLimitSum record);

    int insertSelective(AccountDailyLimitSum record);

    AccountDailyLimitSum selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountDailyLimitSum record);

    int updateByPrimaryKey(AccountDailyLimitSum record);

    int lock(@Param("id") Long id, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    AccountDailyLimitSum selectByGroupId(@Param("groupId") Long groupId);
}