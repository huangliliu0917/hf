package com.hf.core.dao.local;

import com.hf.core.model.po.AccountOprLog;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AccountOprLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(AccountOprLog record);

    int insertSelective(AccountOprLog record);

    AccountOprLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountOprLog record);

    int updateByPrimaryKey(AccountOprLog record);

    int batchInsert(List<AccountOprLog> logs);

    List<AccountOprLog> selectByTradeNo(@Param("outTradeNo") String outTradeNo);

    int updateStatusById(@Param("id") Long id, @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);

    List<AccountOprLog> selectByGroupId(@Param("accountId") Long accountId, @Param("types") List<Integer> types, @Param("statusList") List<Integer> statusList);

    List<AccountOprLog> select(Map<String, Object> params);

    int count(Map<String, Object> params);

    AccountOprLog selectByUnq(@Param("outTradeNo") String outTradeNo, @Param("groupId") Long groupId, @Param("type") Integer type);

    BigDecimal sumLockAmount(@Param("groupIds") List<Long> groupIds, @Param("types") List<Integer> types);
}