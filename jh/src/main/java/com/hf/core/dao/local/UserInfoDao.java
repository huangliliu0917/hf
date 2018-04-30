package com.hf.core.dao.local;

import com.hf.core.model.dto.UserInfoRequest;
import com.hf.core.model.po.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserInfoDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long id);

    List<UserInfo> selectByGroupId(@Param("groupId") Long groupId);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByLoginId(@Param("loginId") String loginId, @Param("password") String password);

    UserInfo selectByLoginIdOnly(@Param("loginId") String loginId);

    UserInfo checkLogin(@Param("loginId") String loginId, @Param("password") String password);

    UserInfo checkUser(@Param("id") Long id, @Param("password") String password);

    UserInfo selectByMerchantNo(@Param("merchantNo") String merchantNo);

    int udpate(UserInfo userInfo);

    int updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword, @Param("password") String password);

    List<UserInfo> select(UserInfoRequest request);

    int resetPassword(Map<String, Object> params);

    UserInfo selectByInviteCode(@Param("inviteCode") String inviteCode);

    int updateStatusById(@Param("id") Long id, @Param("fromStatus") int fromStatus, @Param("targetStatus") int targetStatus);
}