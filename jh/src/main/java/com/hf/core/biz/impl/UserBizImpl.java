package com.hf.core.biz.impl;

import com.hf.base.enums.*;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.model.SalesManDto;
import com.hf.base.model.UserChannelPage;
import com.hf.base.utils.SegmentLock;
import com.hf.base.utils.Utils;
import com.hf.core.biz.UserBiz;
import com.hf.core.biz.service.ArchService;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.UserService;
import com.hf.core.dao.local.*;
import com.hf.core.model.dto.UserGroupDto;
import com.hf.core.model.dto.UserGroupRequest;
import com.hf.core.model.dto.UserInfoDto;
import com.hf.core.model.dto.UserInfoRequest;
import com.hf.core.model.po.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by tengfei on 2017/10/29.
 */
@Service
public class UserBizImpl implements UserBiz {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBankCardDao userBankCardDao;
    @Autowired
    private AdminBankCardDao adminBankCardDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private ArchService archService;

    @Override
    public void register(String loginId, String password, String inviteCode,String subGroupId) {
        UserGroup subUserGroup = null;

        if(StringUtils.isNotEmpty(subGroupId)) {
            subUserGroup = userGroupDao.selectByPrimaryKey(Long.parseLong(subGroupId));
        }

        if(StringUtils.isNotEmpty(inviteCode)) {
            UserInfo subUserInfo = userInfoDao.selectByInviteCode(inviteCode);
            if(!Objects.isNull(subUserInfo)) {
                subUserGroup = userGroupDao.selectByPrimaryKey(subUserInfo.getGroupId());
            }
        }

        if(Objects.isNull(subUserGroup)) {
            subUserGroup = userGroupDao.selectDefaultUserGroup();
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setLoginId(loginId);
        userInfo.setPassword(Utils.convertPassword(password));
        userInfo.setType(UserInfo.TYPE.ADMIN.getValue());

        UserGroup userGroup = new UserGroup();
        userGroup.setType(UserGroup.GroupType.CUSTOMER.getValue());
        userGroup.setSubGroupId(subUserGroup.getId());
        userGroup.setSubGroupName(subUserGroup.getName());
        userGroup.setSubGroupNo(subUserGroup.getGroupNo());
        Long companyId = (subUserGroup.getType()==GroupType.COMPANY.getValue() || subUserGroup.getType() == GroupType.SUPER.getValue())?subUserGroup.getId():subUserGroup.getCompanyId();
        userGroup.setCompanyId(companyId);
        userGroup.setCipherCode(Utils.getRandomString(8));
        String groupNo = archService.getId();
        userGroup.setGroupNo(groupNo);
        userGroup.setStatus(GroupStatus.NEW.getValue());
        userService.register(userGroup,userInfo);
    }

    @Override
    public boolean login(String loginId, String password) {
        password = Utils.convertPassword(password);
        UserInfo userInfo = userInfoDao.checkLogin(loginId,password);
        return !Objects.isNull(userInfo) && !Objects.isNull(userInfo.getId()) && userInfo.getId()>0;
    }

    @Override
    public void edit(UserInfo userInfo) {
        UserInfo user = userInfoDao.selectByPrimaryKey(userInfo.getId());
        int count = userInfoDao.updateByPrimaryKeySelective(userInfo);
        if(count <=0) {
            throw new BizFailException("用户更新失败");
        }
    }

    @Override
    public List<UserInfoDto> getUserList(UserInfoRequest request) {
        List<UserInfo> list = new ArrayList<>();

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(Long.parseLong(request.getAdminId()));
        getUserListByGroup(list,userGroup,request);

        list.parallelStream().distinct();

        List<UserInfoDto> result = new ArrayList<>();

        list.stream().forEach(userInfo -> result.add(buildUserDto(userInfo)));

        return result;
    }

    private void getUserListByGroup(List<UserInfo> list,UserGroup userGroup,UserInfoRequest request) {
        request.setGroupId(userGroup.getId());
        List<UserInfo> groupUserList = userInfoDao.select(request);
        list.addAll(groupUserList);
        List<UserGroup> subUserGroups = userGroupDao.selectBySubGroupId(userGroup.getId());
        if(CollectionUtils.isEmpty(subUserGroups)) {
            return;
        }
        for(UserGroup sub:subUserGroups) {
            getUserListByGroup(list,sub,request);
        }
    }

    private UserInfoDto buildUserDto(UserInfo userInfo) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userInfo.getId());
        userInfoDto.setType(UserInfo.TYPE.parse(userInfo.getType()).getDesc());
        userInfoDto.setStatus(userInfo.getStatus());
        userInfoDto.setStatusDesc(UserInfo.STATUS.parse(userInfo.getStatus()).getDesc());
        userInfoDto.setCreateTime(userInfo.getCreateTime());
        userInfoDto.setName(userInfo.getName());
        userInfoDto.setInviteCode(userInfo.getInviteCode());
        userInfoDto.setInviteSite(String.format("/%s",userInfo.getInviteCode()));
        userInfoDto.setTel(userInfo.getTel());
        UserGroup userGroup = cacheService.getGroup(userInfo.getGroupId());
        if(!Objects.isNull(userGroup)) {
            userInfoDto.setGroup(userGroup.getName());
        }
        return userInfoDto;
    }

    @Override
    public List<UserGroup> getUserGroupList(UserGroupRequest request) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(Long.parseLong(request.getCompanyId()));
        if(userGroup.getType() == UserGroup.GroupType.SUPER.getValue()) {
            request.setCompanyId(null);
        }

        List<UserGroup> list = userGroupDao.select(Utils.bean2Map(request));
        return list;
    }

    private UserGroupDto buildGroup(UserGroup userGroup) {
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setId(userGroup.getId());

        Account account = cacheService.getAccount(userGroup.getId());
        if(!Objects.isNull(account)) {
            userGroupDto.setAmount(account.getAmount());
            userGroupDto.setLockAmount(account.getLockAmount());
        }
        userGroupDto.setCompanyId(userGroup.getCompanyId());
        userGroupDto.setCreateTime(userGroup.getCreateTime());
        userGroupDto.setGroupNo(userGroup.getGroupNo());
        userGroupDto.setName(userGroup.getName());
        userGroupDto.setStatus(userGroup.getStatus());
        userGroupDto.setStatusDesc(UserGroup.Status.parse(userGroup.getStatus()).getDesc());
        userGroupDto.setSubGroupId(userGroup.getSubGroupId());
        userGroupDto.setSubGroupName(userGroup.getSubGroupName());
        userGroupDto.setSubGroupNo(userGroup.getSubGroupNo());
        userGroupDto.setType(UserGroup.GroupType.parse(userGroup.getType()).getDesc());
        return userGroupDto;
    }

    @Override
    public void edit(UserGroup userGroup) {
        int count = userGroupDao.updateByPrimaryKeySelective(userGroup);
        if(count<=0) {
            throw new BizFailException("update group failed");
        }
    }

    @Override
    @Transactional
    public void submit(Long userId, Long groupId) {
//        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);
//
//        if(userInfo.getStatus() == UserStatus.NEW.getValue()) {
//            int count = userInfoDao.updateStatusById(userId, UserStatus.NEW.getValue(),UserStatus.SUBMITED.getValue());
//            if(count<=0) {
//                throw new BizFailException("update userinfo failed");
//            }
//        }
        userGroupDao.updateStatusById(groupId, GroupStatus.NEW.getValue(),GroupStatus.SUBMITED.getValue());
//        if(count<=0) {
//            throw new BizFailException("update user group failed");
//        }
    }

    @Override
    public void saveBankCard(UserBankCard userBankCard) {
        SegmentLock<Long> lock = new SegmentLock<>();
        lock.lock(userBankCard.getGroupId());

        try {
            List<UserBankCard> list = userBankCardDao.selectByUser(userBankCard.getGroupId());
            list = list.stream().filter(card -> card.getStatus()== CardStatus.IN_USE.getValue()).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(list)) {
                userBankCard.setStatus(10);
            }

            userBankCardDao.insertSelective(userBankCard);
        } finally {
            lock.unlock(userBankCard.getGroupId());
        }

    }

    @Override
    public void saveAdminBankCard(AdminBankCard adminBankCard) {
        if(adminBankCard.getId()!= null) {
            int count = adminBankCardDao.updateByPrimaryKeySelective(adminBankCard);
            if(count<=0) {
                throw new BizFailException("update admin card failed");
            }
        } else {
            adminBankCardDao.insertSelective(adminBankCard);
        }
    }

    @Override
    public void userTurnBack(Long groupId, String remark) {
        int count = userGroupDao.updateStatusById(groupId,GroupStatus.SUBMITED.getValue(),GroupStatus.NEW.getValue());
        if(count <= 0) {
            throw new BizFailException("update user group failed");
        }
    }

    @Transactional
    @Override
    public void userPass(Long groupId) {
        //admin bank
//        List<AdminBankCard> cards = adminBankCardDao.selectByGroupId(groupId);
//        if(CollectionUtils.isEmpty(cards)) {
//            throw new BizFailException("结算账号未设置");
//        }

        List<UserChannel> channels = userChannelDao.selectByGroupId(groupId);
        if(CollectionUtils.isEmpty(channels)) {
            throw new BizFailException("用户渠道未设置");
        }

        int count = userGroupDao.updateStatusById(groupId,GroupStatus.SUBMITED.getValue(),GroupStatus.AVAILABLE.getValue());
        if(count<=0) {
            throw new BizFailException("update user group status failed");
        }
//        UserInfoRequest request = new UserInfoRequest();
//        request.setGroupId(groupId);
//        request.setType(UserType.ADMIN.getValue());
//        request.setStatus(UserStatus.SUBMITED.getValue());
//        List<UserInfo> list =  userInfoDao.select(request);
//        for(UserInfo userInfo:list) {
//            count = userInfoDao.updateStatusById(userInfo.getId(),UserStatus.SUBMITED.getValue(),UserStatus.AVAILABLE.getValue());
//            if(count<=0) {
//                throw new BizFailException("update userInfo status failed");
//            }
//        }

        Account account = new Account();
        account.setGroupId(groupId);
        accountDao.insertSelective(account);
    }

    @Transactional
    @Override
    public void saveAminGroup(UserGroup userGroup) {
        userGroup.setType(com.hf.base.model.UserGroup.GroupType.COMPANY.getValue());
        userGroup.setCipherCode(Utils.getRandomString(10));
        userGroup.setGroupNo(archService.getId());
        userGroupDao.insertSelective(userGroup);
        AdminAccount adminAccount = new AdminAccount();
        adminAccount.setGroupId(userGroup.getId());
        adminAccountDao.insertSelective(adminAccount);
    }

    @Override
    public void saveUserGroup(UserGroup userGroup) {
        Long subGroupId = userGroup.getSubGroupId();
        UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(subGroupId);
        if(!Objects.isNull(subUserGroup) && subUserGroup.getStatus() == GroupStatus.AVAILABLE.getValue()) {
            userGroup.setSubGroupNo(subUserGroup.getGroupNo());
            userGroup.setSubGroupId(subUserGroup.getId());
            userGroup.setSubGroupName(subUserGroup.getName());
            Long companyId = (subUserGroup.getType() == GroupType.COMPANY.getValue() || subUserGroup.getType() == GroupType.SUPER.getValue()) ? subUserGroup.getId(): subUserGroup.getCompanyId();
            userGroup.setCompanyId(companyId);
        }

        userGroup.setStatus(GroupStatus.SUBMITED.getValue());

        if(!Objects.isNull(userGroup.getId())) {
            int count = userGroupDao.updateByPrimaryKeySelective(userGroup);
            if(count<=0) {
                throw new BizFailException("update user group failed");
            }
        } else {
            if(StringUtils.isEmpty(userGroup.getCipherCode())) {
                userGroup.setCipherCode(Utils.getRandomString(10));
            }
            userGroup.setGroupNo(archService.getId());
            userGroupDao.insertSelective(userGroup);
        }
    }

    @Override
    public List<UserChannelPage> getUserChannelInfo(Long groupId) {
        List<UserGroupExt> userGroupExts = userGroupExtDao.selectByGroupId(groupId);
        List<UserChannelPage> pages = new ArrayList<>();
        userGroupExts.parallelStream().forEach(userGroupExt -> {
            UserChannelPage userChannelPage = new UserChannelPage();
            pages.add(userChannelPage);
            com.hf.base.model.UserGroupExt ext = new com.hf.base.model.UserGroupExt();
            try {
                BeanUtils.copyProperties(ext,userGroupExt);
                userChannelPage.setUserGroupExt(ext);
            } catch (Exception e) {
                throw new BizFailException(e.getMessage());
            }

            List<UserChannel> channels = userChannelDao.selectByGroupIdProvider(groupId,userGroupExt.getProviderCode());
            List<com.hf.base.model.UserChannel> userChannels = new ArrayList<>();

            channels.parallelStream().forEach(userChannel -> {
                com.hf.base.model.UserChannel channel = new com.hf.base.model.UserChannel();
                try {
                    BeanUtils.copyProperties(channel,userChannel);
                    userChannels.add(channel);
                } catch (Exception e) {
                    throw new BizFailException(e.getMessage());
                }
            });

            userChannelPage.setUserChannels(userChannels);

        });
        return pages;
    }

    @Override
    public List<SalesManDto> getSaleList(Long groupId) {
        List<UserGroup> saleList = userGroupDao.selectBySubGroupId(groupId);
        List<SalesManDto> list = new ArrayList<>();
        saleList.stream().filter(userGroup -> userGroup.getType()==GroupType.SALES.getValue()).forEach(userGroup -> list.add(buildSale(userGroup)));
        return list;
    }

    private SalesManDto buildSale(UserGroup userGroup) {
        SalesManDto salesManDto = new SalesManDto();
        List<UserInfo> userInfos = userInfoDao.selectByGroupId(userGroup.getId());
        UserInfo userInfo = userInfos.get(0);
        salesManDto.setId(userGroup.getId());
        salesManDto.setAddress(userGroup.getAddress());
        salesManDto.setGroupNo(userGroup.getGroupNo());
        salesManDto.setIdCard(userGroup.getIdCard());
        salesManDto.setInviteCode(userInfo.getInviteCode());
        salesManDto.setCreateTime(userInfo.getCreateTime());
        salesManDto.setName(userInfo.getName());
        salesManDto.setQq(userInfo.getQq());
        salesManDto.setSubGroup(userGroup.getSubGroupName());
        salesManDto.setTel(userInfo.getTel());
        salesManDto.setLoginId(userInfo.getLoginId());
        return salesManDto;
    }

    @Transactional
    @Override
    public void editSubGroup(Long groupId, Long subGroupId) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        if(Objects.isNull(userGroup)) {
            throw new BizFailException("用户不存在");
        }

        UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(subGroupId);
        if(Objects.isNull(subUserGroup)) {
            throw new BizFailException("上级用户不存在");
        }

        List<UserChannel> userChannels = userChannelDao.selectByGroupId(groupId);
        if(CollectionUtils.isNotEmpty(userChannels)) {
            for(UserChannel userChannel:userChannels) {

                UserChannel updateUserChannel = new UserChannel();
                updateUserChannel.setId(userChannel.getId());
                updateUserChannel.setSubGroupId(subGroupId);
                UserChannel subUserChannel = userChannelDao.selectByGroupChannel(subGroupId,userChannel.getChannelId());
                if(Objects.isNull(subUserChannel)) {
                    updateUserChannel.setSubFeeRate(new BigDecimal("0"));
                    updateUserChannel.setStatus(ChannelStatus.DELETED.getStatus());
                } else {
                    updateUserChannel.setSubFeeRate(subUserChannel.getFeeRate());
                    BigDecimal subFeeRate = subUserChannel.getFeeRate();
                    if(userChannel.getFeeRate().compareTo(subFeeRate)<=0) {
                        updateUserChannel.setFeeRate(subFeeRate.add(new BigDecimal("0.1")));
                    }
                }
                int count = userChannelDao.updateByPrimaryKeySelective(updateUserChannel);
                if(count<=0) {
                    throw new BizFailException("更新失败");
                }
            }
        }

        UserGroup updateUserGroup = new UserGroup();
        updateUserGroup.setId(groupId);
        updateUserGroup.setSubGroupId(subGroupId);
        updateUserGroup.setSubGroupName(subUserGroup.getName());
        updateUserGroup.setSubGroupNo(subUserGroup.getGroupNo());

        int count = userGroupDao.updateByPrimaryKeySelective(updateUserGroup);
        if(count<=0) {
            throw new BizFailException("更新失败");
        }
    }
}
