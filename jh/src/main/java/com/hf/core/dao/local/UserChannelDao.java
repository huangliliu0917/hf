package com.hf.core.dao.local;

import com.hf.core.model.po.UserChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserChannelDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserChannel record);

    int insertSelective(UserChannel record);

    UserChannel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserChannel record);

    int updateByPrimaryKey(UserChannel record);

    List<UserChannel> selectByGroupId(@Param("groupId") Long groupId);

    List<UserChannel> selectByGroupIdProvider(@Param("groupId") Long groupId, @Param("providerCode") String providerCode);

    UserChannel selectByGroupChannel(@Param("groupId") Long groupId, @Param("channelId") Long channelId);

    UserChannel selectByGroupChannelCode(@Param("groupId") Long groupId, @Param("channelCode") String channelCode, @Param("providerCode") String providerCode);

    List<UserChannel> selectByGroupIdList(@Param("channelId") Long channelId, @Param("groupIds") List<Long> groupIds);
}