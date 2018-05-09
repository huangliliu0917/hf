package com.hf.core.biz;

import com.hf.base.model.SalesManDto;
import com.hf.base.model.SubGroupRequest;
import com.hf.base.model.UserChannelPage;
import com.hf.base.utils.Pagenation;
import com.hf.core.model.dto.UserGroupDto;
import com.hf.core.model.dto.UserGroupRequest;
import com.hf.core.model.dto.UserInfoDto;
import com.hf.core.model.dto.UserInfoRequest;
import com.hf.core.model.po.AdminBankCard;
import com.hf.core.model.po.UserBankCard;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/29.
 */
public interface UserBiz {
    void register(String loginId, String password, String inviteCode, String subGroupId);

    void register(Map<String,String> map);

    boolean login(String loginId, String password);

    void edit(UserInfo userInfo);

    void edit(UserGroup userGroup);

    List<UserInfoDto> getUserList(UserInfoRequest request);

    List<UserGroup> getUserGroupList(UserGroupRequest request);

    void submit(Long userId, Long groupId);

    void saveBankCard(UserBankCard userBankCard);

    void saveAdminBankCard(AdminBankCard adminBankCard);

    void userTurnBack(Long groupId, String remark);

    void userPass(Long groupId);

    void saveAminGroup(UserGroup userGroup);

    void saveUserGroup(UserGroup userGroup);

    List<UserChannelPage> getUserChannelInfo(Long groupId);

    List<SalesManDto> getSaleList(Long groupId);

    void editSubGroup(Long groupId, Long subGroupId);

    Pagenation<UserGroupDto> getSubUserGroups(SubGroupRequest subGroupRequest);
}
