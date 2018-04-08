package com.hf.core.dao.local;

import com.hf.core.model.po.UserGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserGroupDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserGroup record);

    int insertSelective(UserGroup record);

    UserGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserGroup record);

    int updateByPrimaryKey(UserGroup record);

    UserGroup selectByGroupNo(@Param("groupNo") String groupNo);

    List<UserGroup> selectByGroupNos(@Param("groupNos") Set<String> groupNos);

    List<UserGroup> select(Map<String, Object> map);
    List<UserGroup> select1(Map<String, Object> map);

    List<UserGroup> selectBySubGroupId(@Param("subGroupId") Long subGroupId);

    List<UserGroup> selectAdminBySubGroupId(@Param("subGroupId") Long subGroupId);

    List<UserGroup> selectByCompanyId(@Param("companyId") Long companyId);

    UserGroup selectDefaultUserGroup();

    int updateStatusById(@Param("id") Long id, @Param("fromStatus") int fromStatus, @Param("targetStatus") int targetStatus);

    UserGroup selectByNo(@Param("groupNo") String groupNo);

    List<UserGroup> selectByIds(@Param("ids") Set<Long> ids);
}