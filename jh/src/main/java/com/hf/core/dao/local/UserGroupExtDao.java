package com.hf.core.dao.local;

import com.hf.core.model.po.UserGroupExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserGroupExtDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserGroupExt record);

    int insertSelective(UserGroupExt record);

    UserGroupExt selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserGroupExt record);

    int updateByPrimaryKey(UserGroupExt record);

    UserGroupExt selectByUnq(@Param("groupId") Long groupId, @Param("providerCode") String providerCode);

    List<UserGroupExt> selectByGroupId(@Param("groupId") Long groupId);
}