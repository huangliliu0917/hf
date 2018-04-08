package com.hf.core.dao.local;

import com.hf.core.model.po.AccountDailyLimit;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDailyLimitDao {
    int deleteByPrimaryKey(Long id);

    int insert(AccountDailyLimit record);

    int insertSelective(AccountDailyLimit record);

    AccountDailyLimit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountDailyLimit record);

    int updateByPrimaryKey(AccountDailyLimit record);

    List<AccountDailyLimit> selectByGroupId(@Param("groupId") Long groupId);

    int lock(@Param("id") Long id, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    AccountDailyLimit selectAvailableAccount(@Param("groupId") Long groupId, @Param("amount") BigDecimal amount);
}