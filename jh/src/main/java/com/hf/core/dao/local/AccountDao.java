package com.hf.core.dao.local;

import com.hf.core.model.po.Account;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AccountDao {
    int deleteByPrimaryKey(Long id);

    int insert(Account record);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);

    Account selectByGroupId(@Param("groupId") Long userId);

    int addAmount(@Param("id") Long id, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    List<Account> selectByGroupIds(@Param("groupIds") List<Long> groupIds);

    List<Account> select(Map<String, Object> params);

    int count(Map<String, Object> params);

    int lockAmount(@Param("id") Long id, @Param("lockAmount") BigDecimal lockAmount, @Param("version") Integer version);

    int unlockAmount(@Param("id") Long id, @Param("lockAmount") BigDecimal lockAmount, @Param("version") Integer version);

    int finishWithDraw(@Param("id") Long id, @Param("lockAmount") BigDecimal lockAmount, @Param("version") Integer version);

    int finishTax(@Param("id") Long id, @Param("lockAmount") BigDecimal lockAmount, @Param("version") Integer version);

    List<Account> selectAvaAccount();
}