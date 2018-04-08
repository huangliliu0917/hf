package com.hf.core.dao.local;

import com.hf.core.model.po.SettleTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SettleTaskDao {
    int deleteByPrimaryKey(Long id);

    int insert(SettleTask record);

    int insertSelective(SettleTask record);

    SettleTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SettleTask record);

    int updateByPrimaryKey(SettleTask record);

    int updateStatusById(@Param("id") Long id, @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);

    List<SettleTask> select(Map<String, Object> params);

    int count(Map<String, Object> params);
}