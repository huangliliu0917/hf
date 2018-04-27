package com.hf.core.biz.impl;

import com.hf.base.contants.Constants;
import com.hf.base.enums.*;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.model.WithDrawInfo;
import com.hf.base.model.WithDrawRequest;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Pagenation;
import com.hf.core.biz.SettleBiz;
import com.hf.core.biz.service.AccountService;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.UserService;
import com.hf.core.dao.local.*;
import com.hf.core.model.po.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SettleBizImpl implements SettleBiz {
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBankCardDao userBankCardDao;
    @Autowired
    private AdminBankCardDao adminBankCardDao;

    @Override
    public void saveSettle(SettleTask settleTask) {
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(settleTask.getGroupId());
        Account account = accountDao.selectByGroupId(settleTask.getGroupId());

        UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(userGroup.getCompanyId());
        AdminAccount payAccount = adminAccountDao.selectByGroupId(subUserGroup.getId());

        AdminBankCard payBankCard = adminBankCardDao.selectByCompanyId(userGroup.getCompanyId(),userGroup.getId());
        settleTask.setPayAccountId(payAccount.getId());
        BigDecimal feeRate = new BigDecimal(cacheService.getProp(Constants.SETTLE_FEE_RATE,"5"));
        BigDecimal fee = settleTask.getSettleAmount().multiply(feeRate).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal payAmount = settleTask.getSettleAmount().subtract(fee);

        settleTask.setAccountId(account.getId());
        settleTask.setFee(fee);
        settleTask.setFeeRate(feeRate);
        settleTask.setPayAmount(payAmount);
        settleTask.setPayBankCard(payBankCard==null?0L:payBankCard.getId());
        settleTask.setPayGroupId(userGroup.getCompanyId());

        settleTaskDao.insertSelective(settleTask);

        accountService.settle(settleTask.getId());
    }

    @Transactional
    @Override
    public void finishSettle(Long id) {
        SettleTask settleTask = settleTaskDao.selectByPrimaryKey(id);
        //支付金额
        AccountOprLog withdrawAccountLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.WITHDRAW.getValue());
        int count = accountOprLogDao.updateStatusById(withdrawAccountLog.getId(), OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update oprLog status failed");
        }

        Account withdrawAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        count = accountDao.finishWithDraw(withdrawAccount.getId(),withdrawAccountLog.getAmount(),withdrawAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("finish withdraw failed");
        }

        //手续费
        AccountOprLog feeLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.TAX.getValue());
        count = accountOprLogDao.updateStatusById(feeLog.getId(),OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update feelog status failed");
        }

        withdrawAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        count = accountDao.finishTax(withdrawAccount.getId(),feeLog.getAmount(),withdrawAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("update fee account failed");
        }

        //admin log
        AdminAccountOprLog adminLog = adminAccountOprLogDao.selectByNo(String.valueOf(settleTask.getId()));
        if(adminLog.getType() != OprType.WITHDRAW.getValue()) {
            throw new BizFailException("opr log type not match");
        }

        count = adminAccountOprLogDao.updateStatusById(adminLog.getId(),OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update admin Opr log status failed");
        }

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        count = adminAccountDao.finishPay(adminAccount.getId(),adminLog.getAmount(),adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("update admin amount failed");
        }

        //admin true pay log
        AccountOprLog payLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getPayGroupId(),OprType.FEE.getValue());
        count = accountOprLogDao.updateStatusById(payLog.getId(),OprStatus.NEW.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException("update opr log failed");
        }

        Account payAccount = accountDao.selectByGroupId(settleTask.getPayGroupId());
        count = accountDao.addAmount(payAccount.getId(),payLog.getAmount(),payAccount.getVersion());

        if(count<=0) {
            throw new BizFailException("update pay Account failed");
        }

        count = settleTaskDao.updateStatusById(settleTask.getId(), SettleStatus.PROCESSING.getValue(),SettleStatus.SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException("update settle status failed");
        }
    }

    @Transactional
    @Override
    public void settleFailed(Long id) {
        SettleTask settleTask = settleTaskDao.selectByPrimaryKey(id);
        //支付金额
        AccountOprLog withdrawAccountLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.WITHDRAW.getValue());
        int count = accountOprLogDao.updateStatusById(withdrawAccountLog.getId(), OprStatus.NEW.getValue(),OprStatus.PAY_FAILED.getValue());
        if(count<=0) {
            throw new BizFailException("update oprLog status failed");
        }
        Account withdrawAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        count = accountDao.unlockAmount(withdrawAccount.getId(),withdrawAccountLog.getAmount(),withdrawAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("unlock withdraw failed");
        }

        //手续费
        AccountOprLog feeLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.TAX.getValue());
        count = accountOprLogDao.updateStatusById(feeLog.getId(),OprStatus.NEW.getValue(),OprStatus.PAY_FAILED.getValue());
        if(count<=0) {
            throw new BizFailException("update feelog status failed");
        }

        withdrawAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        count = accountDao.unlockAmount(withdrawAccount.getId(),feeLog.getAmount(),withdrawAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("update fee account failed");
        }

        //admin log
        AdminAccountOprLog adminLog = adminAccountOprLogDao.selectByNo(String.valueOf(settleTask.getId()));
        if(adminLog.getType() != OprType.WITHDRAW.getValue()) {
            throw new BizFailException("opr log type not match");
        }

        count = adminAccountOprLogDao.updateStatusById(adminLog.getId(),OprStatus.NEW.getValue(),OprStatus.PAY_FAILED.getValue());
        if(count<=0) {
            throw new BizFailException("update admin Opr log status failed");
        }

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        count = adminAccountDao.unlockAmount(adminAccount.getId(),adminLog.getAmount(),adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("update admin amount failed");
        }

        //admin true pay log
        AccountOprLog payLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getPayGroupId(),OprType.FEE.getValue());
        count = accountOprLogDao.updateStatusById(payLog.getId(),OprStatus.NEW.getValue(),OprStatus.PAY_FAILED.getValue());
        if(count<=0) {
            throw new BizFailException("update opr log failed");
        }

        count = settleTaskDao.updateStatusById(settleTask.getId(), SettleStatus.PROCESSING.getValue(),SettleStatus.FAILED.getValue());
        if(count<=0) {
            throw new BizFailException("update settle status failed");
        }
    }

    @Override
    public Pagenation<WithDrawInfo> getWithDrawPage(WithDrawRequest withDrawRequest) {
        Map<String,Object> params = buildWithDrawMap(withDrawRequest);

        List<SettleTask> tasks = settleTaskDao.select(params);
        if(CollectionUtils.isEmpty(tasks)) {
            return new Pagenation<WithDrawInfo>(new ArrayList<>());
        }

        int count = settleTaskDao.count(params);

        Set<Long> withdrawGroupIds = tasks.parallelStream().map(SettleTask::getGroupId).collect(Collectors.toSet());
        List<UserGroup> withDrawUserGroups = userGroupDao.selectByIds(withdrawGroupIds);
        Set<Long> payGroupIds = tasks.parallelStream().map(SettleTask::getPayGroupId).collect(Collectors.toSet());
        List<UserGroup> payUserGroups = userGroupDao.selectByIds(payGroupIds);

        Map<Long,UserGroup> withDrawGroupMap = withDrawUserGroups.parallelStream().collect(Collectors.toMap(UserGroup::getId, Function.identity()));
        Map<Long,UserGroup> payGroupMap = payUserGroups.parallelStream().collect(Collectors.toMap(UserGroup::getId,Function.identity()));

        List<WithDrawInfo> result = new ArrayList<>();
        tasks.forEach(settleTask -> result.add(buildWithDrawInfo(settleTask,withDrawGroupMap,payGroupMap)));

        return new Pagenation<>(result,count,withDrawRequest.getCurrentPage(),withDrawRequest.getPageSize());
    }

    private Map<String,Object> buildWithDrawMap(WithDrawRequest withDrawRequest) {
        Integer startIndex = (withDrawRequest.getCurrentPage()-1)*withDrawRequest.getPageSize();
        Map<String,Object> params = MapUtils.buildMap("status",withDrawRequest.getStatus(),
                "startIndex",startIndex,
                "pageSize",withDrawRequest.getPageSize(),
                "mchId",withDrawRequest.getMchId());
        WithDrawRole withDrawRole = WithDrawRole.parse(withDrawRequest.getRole());
        switch (withDrawRole) {
            case PAYER:
                params.put("payGroupId",withDrawRequest.getGroupId());
                break;
            case DRAWER:
                List<UserGroup> childGroups = userService.getChildMchIds(withDrawRequest.getGroupId());
                List<Long> groupIds = childGroups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
                params.put("groupIds",groupIds);
                break;
        }
        return params;
    }

    private WithDrawInfo buildWithDrawInfo(SettleTask task,Map<Long,UserGroup> withDrawGroupMap,Map<Long,UserGroup> payGroupMap) {
        WithDrawInfo withDrawInfo = new WithDrawInfo();
        withDrawInfo.setId(task.getId());
        withDrawInfo.setName(withDrawGroupMap.get(task.getGroupId()).getName());
        withDrawInfo.setGroupId(task.getGroupId());
        withDrawInfo.setAccountId(task.getAccountId());

        com.hf.base.model.UserBankCard card = new com.hf.base.model.UserBankCard();
        card.setBank(task.getBank());
        card.setBankNo(task.getBankNo());
        card.setOwner(task.getOwner());
        card.setDeposit(task.getDeposit());
        withDrawInfo.setWithdrawBank(card);

        withDrawInfo.setFee(task.getFee().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        withDrawInfo.setFeeRate(task.getFeeRate());
        withDrawInfo.setSettleAmount(task.getSettleAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        withDrawInfo.setPayAmount(task.getPayAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));

        withDrawInfo.setPayAccountId(task.getPayAccountId());
        withDrawInfo.setPayGroupId(task.getPayGroupId());
        withDrawInfo.setPayName(payGroupMap.get(task.getPayGroupId()).getName());

        AdminBankCard adminBankCard = adminBankCardDao.selectByPrimaryKey(task.getPayBankCard());
        if(Objects.isNull(adminBankCard)) {
            com.hf.base.model.AdminBankCard adminCard = new com.hf.base.model.AdminBankCard();
            withDrawInfo.setPayBank(adminCard);
        } else {
            com.hf.base.model.AdminBankCard adminCard = new com.hf.base.model.AdminBankCard();
            BeanUtils.copyProperties(adminBankCard,adminCard);
            withDrawInfo.setPayBank(adminCard);
        }

        withDrawInfo.setStatus(task.getStatus());
        withDrawInfo.setStatusDesc(SettleStatus.parse(task.getStatus()).getDesc());
        withDrawInfo.setCreateTime(task.getCreateTime());
        withDrawInfo.setUpdateTime(task.getUpdateTime());
        withDrawInfo.setGroupNo(withDrawGroupMap.get(task.getGroupId()).getGroupNo());
        //todo add remark to task
        withDrawInfo.setRemark("");

        withDrawInfo.setGroupType(withDrawGroupMap.get(task.getGroupId()).getType());
        withDrawInfo.setGroupTypeDesc(GroupType.parse(withDrawGroupMap.get(task.getGroupId()).getType()).getDesc());
        return withDrawInfo;
    }
}
