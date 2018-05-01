package com.hf.core.biz.service.impl;

import com.hf.base.enums.GroupType;
import com.hf.base.enums.UserStatus;
import com.hf.core.biz.service.UserService;
import com.hf.core.dao.local.*;
import com.hf.core.model.po.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultUserService implements UserService {
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private ChannelProviderDao channelProviderDao;
    @Autowired
    private UserChannelAccountDao userChannelAccountDao;
    @Autowired
    private AccountDao accountDao;

    @Transactional
    @Override
    public void register(UserGroup userGroup, UserInfo userInfo) {
        userGroupDao.insertSelective(userGroup);
        userInfo.setGroupId(userGroup.getId());
        userInfo.setInviteCode(RandomStringUtils.random(16, 20, 110, true, true));
        userInfo.setStatus(UserStatus.AVAILABLE.getValue());
        userInfoDao.insertSelective(userInfo);
    }

    @Override
    public List<UserGroup> getChildMchIds(Long groupId) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        GroupType groupType = GroupType.parse(userGroup.getType());

        List<UserGroup> allGroups = new ArrayList<>();

        switch (groupType) {
            case AGENT:
                allGroups.add(userGroup);
                allGroups = getChild(allGroups,userGroup);
                break;
            case CUSTOMER:
                allGroups.add(userGroup);
                allGroups = getChild(allGroups,userGroup);
                break;
            case SUPER:
                break;
            case COMPANY:
                allGroups = userGroupDao.selectByCompanyId(userGroup.getId());
                break;
        }
        return allGroups;
    }

    private List<UserGroup> getChild(List<UserGroup> list,UserGroup userGroup) {
        List<UserGroup> children = userGroupDao.selectBySubGroupId(userGroup.getId());
        if(CollectionUtils.isEmpty(children)) {
            return list;
        }

        list.addAll(children);

        for(UserGroup child:children) {
            getChild(list,child);
        }
        return list;
    }

    @Override
    public List<UserGroup> getChildCompany(Long groupId) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        if(Objects.isNull(userGroup) || (userGroup.getType() != GroupType.SUPER.getValue() && userGroup.getType() != GroupType.COMPANY.getValue())) {
            return Collections.EMPTY_LIST;
        }

        List<UserGroup> list = new ArrayList<>();
        list.add(userGroup);

        getAdminChild(list,userGroup);
        return list;
    }

    private List<UserGroup> getAdminChild(List<UserGroup> list,UserGroup userGroup) {
        List<UserGroup> children = userGroupDao.selectAdminBySubGroupId(userGroup.getId());

        if(CollectionUtils.isEmpty(children)) {
            return list;
        }

        list.addAll(children);

        for(UserGroup child:children) {
            getAdminChild(list,child);
        }
        return list;
    }

    @Transactional
    @Override
    public void saveUserGroupExt(UserGroupExt userGroupExt) {
        if(userGroupExt.getId()!= null && userGroupExt.getId()>0L) {
            userGroupExtDao.updateByPrimaryKeySelective(userGroupExt);
        } else {
            ChannelProvider channelProvider = channelProviderDao.selectByCode(userGroupExt.getProviderCode());
            userGroupExt.setProviderName(channelProvider.getProviderName());
            userGroupExtDao.insertSelective(userGroupExt);
        }

        userGroupExt = userGroupExtDao.selectByPrimaryKey(userGroupExt.getId());

        UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(userGroupExt.getGroupId(),userGroupExt.getProviderCode());
        if(Objects.isNull(userChannelAccount)) {
            Account account = accountDao.selectByGroupId(userGroupExt.getGroupId());
            userChannelAccount = new UserChannelAccount();
            userChannelAccount.setAccountId(account.getId());
            userChannelAccount.setChannelProvider(userGroupExt.getProviderCode());
            userChannelAccountDao.insertSelective(userChannelAccount);
        }
    }
}
