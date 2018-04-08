package com.hf.core.dao.local;

import com.hf.core.model.po.AdminBankCard;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AdminBankCardDao {
    int deleteByPrimaryKey(Long id);

    int insert(AdminBankCard record);

    int insertSelective(AdminBankCard record);

    AdminBankCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AdminBankCard record);

    int updateByPrimaryKey(AdminBankCard record);

    AdminBankCard selectByCompanyId(@Param("companyId") Long companyId, @Param("groupId") Long groupId);

    List<AdminBankCard> selectByGroupId(@Param("groupId") Long groupId);

    AdminBankCard selectAvailableCard(@Param("groupId") Long groupId, @Param("amount") BigDecimal amount);

    List<AdminBankCard> select(Map<String, Object> map);
}