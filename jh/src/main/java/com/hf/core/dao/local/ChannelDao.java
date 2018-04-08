package com.hf.core.dao.local;

import com.hf.core.model.po.Channel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChannelDao {
    int deleteByPrimaryKey(Long id);

    int insert(Channel record);

    int insertSelective(Channel record);

    Channel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Channel record);

    int updateByPrimaryKey(Channel record);

    List<Channel> selectForList();

    List<Channel> selectForAvaList();

    Channel selectByCode(@Param("code") String code, @Param("providerCode") String providerCode);

    List<Channel> selectByProviderCode(@Param("providerCode") String providerCode);
}