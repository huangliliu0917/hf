package com.hf.core.dao.local;

import com.hf.core.model.po.AgentPayLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AgentPayLogDao {
    int deleteByPrimaryKey(Long id);

    int insert(AgentPayLog record);

    int insertSelective(AgentPayLog record);

    AgentPayLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentPayLog record);

    int updateByPrimaryKey(AgentPayLog record);

    List<AgentPayLog> select(Map<String,Object> map);

    int updateStatus(@Param("id")Long id,@Param("fromStatus")Integer fromStatus,@Param("toStatus")Integer toStatus);
}