package com.hf.core.dao.local;

import com.hf.core.model.po.AdminAccountOprLog;
import org.apache.ibatis.annotations.Param;

public interface AdminAccountOprLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(AdminAccountOprLog record);

    int insertSelective(AdminAccountOprLog record);

    AdminAccountOprLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AdminAccountOprLog record);

    int updateByPrimaryKey(AdminAccountOprLog record);

    AdminAccountOprLog selectByNo(@Param("outTradeNo") String outTradeNo);

    int updateStatusById(@Param("id") Long id, @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);
}