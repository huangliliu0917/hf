package com.hf.core.dao.local;

import com.hf.core.model.po.ChannelProvider;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChannelProviderDao {
    int deleteByPrimaryKey(Long id);

    int insert(ChannelProvider record);

    int insertSelective(ChannelProvider record);

    ChannelProvider selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChannelProvider record);

    int updateByPrimaryKey(ChannelProvider record);

    ChannelProvider selectByCode(@Param("code") String code);

    List<ChannelProvider> selectAgentPay();

    List<ChannelProvider> selectAll();
}