package com.hf.core.dao.local;

import com.hf.core.model.po.AdminAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AdminAccountDao {
    int deleteByPrimaryKey(Long id);

    int insert(AdminAccount record);

    int insertSelective(AdminAccount record);

    AdminAccount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AdminAccount record);

    int updateByPrimaryKey(AdminAccount record);

    AdminAccount selectByGroupId(@Param("groupId") Long groupId);

    int addAmount(@Param("id") Long id, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    List<AdminAccount> selectByGroupIds(@Param("groupIds") List<Long> groupIds);

    int lockAmount(@Param("id") Long id, @Param("lockAmount") BigDecimal lockAmount, @Param("version") Integer version);

    int unlockAmount(@Param("id") Long id, @Param("lockAmount") BigDecimal lockAmount, @Param("version") Integer version);

    int finishPay(@Param("id") Long id, @Param("lockAmount") BigDecimal lockAmount, @Param("version") Integer version);
}