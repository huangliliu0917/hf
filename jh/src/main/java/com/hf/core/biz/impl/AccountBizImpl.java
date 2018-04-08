package com.hf.core.biz.impl;

import com.hf.base.enums.GroupStatus;
import com.hf.base.enums.GroupType;
import com.hf.base.enums.OprStatus;
import com.hf.base.enums.OprType;
import com.hf.base.model.*;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Pagenation;
import com.hf.core.biz.AccountBiz;
import com.hf.core.biz.service.UserService;
import com.hf.core.dao.local.AccountDao;
import com.hf.core.dao.local.AccountOprLogDao;
import com.hf.core.dao.local.AdminAccountDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.model.dto.AccountOprQueryRequest;
import com.hf.core.model.dto.AccountOprQueryResponse;
import com.hf.core.model.po.Account;
import com.hf.core.model.po.AccountOprLog;
import com.hf.core.model.po.AdminAccount;
import com.hf.core.model.po.UserGroup;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by tengfei on 2017/11/4.
 */
@Service
public class AccountBizImpl implements AccountBiz {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private AdminAccountDao adminAccountDao;

    @Override
    public List<AccountOprQueryResponse> getAccountOprLogs(AccountOprQueryRequest request) {
        return null;
    }

    @Override
    public Pagenation<AccountPageInfo> getAccountPage(AccountRequest accountRequest) {
        int startIndex = (accountRequest.getCurrentPage()-1)*accountRequest.getPageSize();

        List<UserGroup> userGroups = userService.getChildMchIds(accountRequest.getGroupId());
        if(CollectionUtils.isEmpty(userGroups)) {
            userGroups = userGroupDao.select(MapUtils.buildMap("status", GroupStatus.AVAILABLE.getValue(),"type",accountRequest.getGroupType(),"mchId",accountRequest.getMchId(),"name",accountRequest.getName()));
        } else {
            userGroups = userGroups.parallelStream().filter(userGroup -> {
              if(!Objects.isNull(accountRequest.getGroupType())) {
                  if(accountRequest.getGroupType() != userGroup.getType()) {
                      return false;
                  }
              }
              if(!Objects.isNull(accountRequest.getMchId())) {
                  if(!userGroup.getGroupNo().startsWith(accountRequest.getMchId())) {
                    return false;
                  }
              }
              if(!Objects.isNull(accountRequest.getName())) {
                  if(!userGroup.getName().startsWith(accountRequest.getName())) {
                      return false;
                  }
              }
              return true;
            }).collect(Collectors.toList());
        }

        Map<Long,UserGroup> groupMap = userGroups.parallelStream().collect(Collectors.toMap(UserGroup::getId, Function.identity()));
        List<Long> groupIds = userGroups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());

        Map<String,Object> params = MapUtils.buildMap("status",accountRequest.getStatus(),"startIndex",startIndex,"pageSize",accountRequest.getPageSize(),"groupIds",groupIds);
        List<Account> accounts = accountDao.select(params);

        if(CollectionUtils.isEmpty(accounts)) {
            return new Pagenation<AccountPageInfo>(new ArrayList<>(),0,accountRequest.getCurrentPage(),accountRequest.getPageSize());
        }

        Integer count = accountDao.count(params);
        List<AccountPageInfo> list = new ArrayList<>();
        accounts.stream().forEach(account -> list.add(buildPageInfo(account,groupMap)));

        return new Pagenation<>(list,count,accountRequest.getCurrentPage(),accountRequest.getPageSize());
    }

    private AccountPageInfo buildPageInfo(Account account,Map<Long,UserGroup> groupMap) {
        AccountPageInfo accountPageInfo = new AccountPageInfo();
        accountPageInfo.setAmount( (account.getAmount().subtract(account.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        accountPageInfo.setLockAmount(account.getLockAmount());
        List<AccountOprLog> logs = accountOprLogDao.selectByGroupId(account.getId(), OprType.getAddList(), Arrays.asList(OprStatus.PAY_SUCCESS.getValue()));
        BigDecimal processingAmount = logs.parallelStream().map(AccountOprLog::getAmount).reduce(new BigDecimal("0"),BigDecimal::add);
        accountPageInfo.setProcessingAmount(processingAmount);
        accountPageInfo.setTotalAmount(account.getTotalAmount());
        accountPageInfo.setTotalSettleAmount(account.getPaidAmount());
        accountPageInfo.setTotalFee(account.getFee());
        accountPageInfo.setCreateTime(account.getCreateTime());
        accountPageInfo.setGroupId(account.getGroupId());
        accountPageInfo.setGroupType(groupMap.get(account.getGroupId()).getType());
        accountPageInfo.setGroupTypeDesc(GroupType.parse(groupMap.get(account.getGroupId()).getType()).getDesc());
        accountPageInfo.setId(account.getId());
        accountPageInfo.setMchId(groupMap.get(account.getGroupId()).getGroupNo());
        accountPageInfo.setName(groupMap.get(account.getGroupId()).getName());
        accountPageInfo.setStatus(account.getStatus());
        accountPageInfo.setStatusDesc(account.getStatus()==0?"可用":"不可用");
        accountPageInfo.setUpdateTime(account.getUpdateTime());
        return accountPageInfo;
    }

    @Override
    public Pagenation<AdminAccountPageInfo> getAdminAccountPage(AccountRequest accountRequest) {
        Long groupId = accountRequest.getGroupId();
        List<UserGroup> groups = userService.getChildCompany(groupId);

         groups = groups.parallelStream().filter(userGroup -> {
            if(StringUtils.isNotEmpty(accountRequest.getMchId())) {
                if(!userGroup.getGroupNo().startsWith(accountRequest.getMchId())) {
                    return false;
                }
            }
            if(StringUtils.isNotEmpty(accountRequest.getName())) {
                if(!userGroup.getName().startsWith(accountRequest.getName())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

         if(CollectionUtils.isEmpty(groups)) {
             return new Pagenation<AdminAccountPageInfo>(new ArrayList<>(),accountRequest.getCurrentPage(),accountRequest.getPageSize());
         }

        Map<Long,UserGroup> groupMap = groups.parallelStream().collect(Collectors.toMap(UserGroup::getId,Function.identity()));

        List<Long> groupIds = groups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
        List<Account> accounts = accountDao.selectByGroupIds(groupIds);
        Map<Long,Account> accountMap = accounts.parallelStream().collect(Collectors.toMap(Account::getGroupId,Function.identity()));
        List<AdminAccount> adminAccounts = adminAccountDao.selectByGroupIds(groupIds);

        List<AdminAccountPageInfo> result = new ArrayList<>();
        adminAccounts.stream().forEach(adminAccount -> result.add(buildAdminAccountInfo(adminAccount,groupMap,accountMap)));

        return new Pagenation<AdminAccountPageInfo>(result,accountRequest.getCurrentPage(),accountRequest.getPageSize());
    }

    private AdminAccountPageInfo buildAdminAccountInfo(AdminAccount adminAccount,Map<Long,UserGroup> groupMap,Map<Long,Account> accountMap) {
        AdminAccountPageInfo adminAccountPageInfo = new AdminAccountPageInfo();
        adminAccountPageInfo.setAmount(adminAccount.getAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        adminAccountPageInfo.setPaidAmount(adminAccount.getPaidAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        adminAccountPageInfo.setCompanyAmount(accountMap.get(adminAccount.getGroupId()).getAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        adminAccountPageInfo.setCustomerAmount((adminAccount.getAmount().subtract(accountMap.get(adminAccount.getGroupId()).getAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));

        List<AccountOprLog> logs = accountOprLogDao.selectByGroupId(accountMap.get(adminAccount.getGroupId()).getId(), OprType.getAddList(), Arrays.asList(OprStatus.PAY_SUCCESS.getValue()));
        BigDecimal processingAmount = logs.parallelStream().map(AccountOprLog::getAmount).reduce(new BigDecimal("0"),BigDecimal::add);

        adminAccountPageInfo.setProcessingAmount(processingAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        adminAccountPageInfo.setTotalAmount(adminAccount.getTotalAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        adminAccountPageInfo.setWithdrawAmount(adminAccount.getLockAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        adminAccountPageInfo.setCreateTime(adminAccount.getCreateTime());
        adminAccountPageInfo.setUpdateTime(adminAccount.getUpdateTime());
        adminAccountPageInfo.setId(adminAccount.getId());
        adminAccountPageInfo.setMchId(groupMap.get(adminAccount.getGroupId()).getGroupNo());
        adminAccountPageInfo.setName(groupMap.get(adminAccount.getGroupId()).getName());

        return adminAccountPageInfo;
    }

    @Override
    public Pagenation<AccountOprInfo> getAccountOprPage(AccountOprRequest accountOprRequest) {
        List<UserGroup> groups = userService.getChildMchIds(accountOprRequest.getGroupId());
        List<Long> groupIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(groups)) {
            groupIds = groups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
        }

        int startIndex = (accountOprRequest.getCurrentPage()-1)*accountOprRequest.getPageSize();
        Map<String,Object> map = MapUtils.buildMap("startIndex",startIndex,"pageSize",accountOprRequest.getPageSize(),
                "oprType",accountOprRequest.getOprType(),"outTradeNo",accountOprRequest.getOutTradeNo(),
                "status",accountOprRequest.getStatus(),"groupIds",groupIds);

        List<AccountOprLog> logs = accountOprLogDao.select(map);
        if(CollectionUtils.isEmpty(logs)) {
            return new Pagenation<AccountOprInfo>(Collections.EMPTY_LIST);
        }

        int count = accountOprLogDao.count(map);
        Set<Long> groupIdSet = logs.parallelStream().map(AccountOprLog::getGroupId).collect(Collectors.toSet());
        List<UserGroup> groupList = userGroupDao.selectByIds(groupIdSet);
        Map<Long,UserGroup> groupMap = groupList.parallelStream().collect(Collectors.toMap(UserGroup::getId,Function.identity()));

        List<AccountOprInfo> oprLogs = new ArrayList<>();
        logs.stream().forEach(log -> oprLogs.add(getOprInfo(log,groupMap)));
        return new Pagenation<AccountOprInfo>(oprLogs,count,accountOprRequest.getCurrentPage(),accountOprRequest.getPageSize());
    }

    private AccountOprInfo getOprInfo(AccountOprLog log,Map<Long,UserGroup> groupMap) {
        AccountOprInfo accountOprInfo = new AccountOprInfo();
        accountOprInfo.setId(log.getId());
        accountOprInfo.setAccountId(log.getAccountId());
        accountOprInfo.setGroupId(log.getGroupId());
        accountOprInfo.setName(null == groupMap.get(log.getGroupId())?"":groupMap.get(log.getGroupId()).getName());
        accountOprInfo.setOprType(log.getType());
        accountOprInfo.setOprTypeDesc(OprType.parse(log.getType()).getDesc());
        accountOprInfo.setOutTradeNo(log.getOutTradeNo());
        accountOprInfo.setStatus(log.getStatus());
        accountOprInfo.setStatusDesc(OprStatus.parse(log.getStatus()).getDesc());
        accountOprInfo.setAmount(log.getAmount());
        accountOprInfo.setOprTime(log.getCreateTime());
        accountOprInfo.setRemark(log.getRemark());
        accountOprInfo.setMchId(null==groupMap.get(log.getGroupId())?"":groupMap.get(log.getGroupId()).getGroupNo());
        accountOprInfo.setAdd(OprType.parse(log.getType()).isAdd());
        return accountOprInfo;
    }

    @Override
    public BigDecimal getLockedAmount(Long groupId) {
        List<UserGroup> groups = userService.getChildMchIds(groupId);
        List<Long> groupIds = groups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
        return accountOprLogDao.sumLockAmount(groupIds,OprType.getAddList());
    }
}
