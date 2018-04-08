package com.hf.core.biz.service.impl;

import com.hf.base.enums.OprStatus;
import com.hf.base.enums.OprType;
import com.hf.base.enums.SettleStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.biz.service.AccountService;
import com.hf.core.dao.local.*;
import com.hf.core.model.dto.RefundResponse;
import com.hf.core.model.dto.ReverseResponse;
import com.hf.core.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by tengfei on 2017/10/28.
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AdminAccountDao adminAccountDao;

    @Override
    public void refund(RefundResponse refundResponse) {

    }

    @Override
    public void reverse(ReverseResponse reverseResponse) {

    }

    @Transactional
    @Override
    public void promote(AccountOprLog log) {
        int count = accountOprLogDao.updateStatusById(log.getId(), OprStatus.PAY_SUCCESS.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update opr log status from 1 to 10 failed,%s",log.getId()));
        }
        Account account = accountDao.selectByPrimaryKey(log.getAccountId());
        count = accountDao.addAmount(account.getId(),log.getAmount(),account.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update account amount failed.opr id:%s",log.getId()));
        }
    }

    @Transactional
    @Override
    public void settle(Long settleId) {
        SettleTask settleTask = settleTaskDao.selectByPrimaryKey(settleId);

        BigDecimal payAmount = settleTask.getPayAmount();
        BigDecimal feeRate = settleTask.getFeeRate();
        BigDecimal fee = settleTask.getFee();

        Account settleAccount = accountDao.selectByGroupId(settleTask.getGroupId());

        //更新账户信息,转出方账户扣减，admin account扣减,company account增加
        AccountOprLog log = new AccountOprLog();
        log.setRemark(String.format("提现金额:%s,手续费率:%s,转账金额:%s",settleTask.getSettleAmount(),feeRate,fee));
        log.setGroupId(settleTask.getGroupId());
        log.setType(OprType.WITHDRAW.getValue());
        log.setAmount(payAmount);
        log.setOutTradeNo(String.valueOf(settleId));
        log.setAccountId(settleTask.getGroupId());

        accountOprLogDao.insertSelective(log);

        int count = accountDao.lockAmount(settleAccount.getId(),payAmount,settleAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update account failed,id:%s",settleAccount.getId()));
        }

        settleAccount = accountDao.selectByPrimaryKey(settleAccount.getId());
        //手续费，提现方
        AccountOprLog taxLog = new AccountOprLog();
        taxLog.setRemark(String.format("提现金额:%s,手续费率:%s,手续费:%s",settleTask.getSettleAmount(),feeRate,fee));
        taxLog.setGroupId(settleTask.getGroupId());
        taxLog.setType(OprType.TAX.getValue());
        taxLog.setAmount(fee);
        taxLog.setOutTradeNo(String.valueOf(settleId));
        taxLog.setAccountId(settleTask.getAccountId());

        accountOprLogDao.insertSelective(taxLog);
        count = accountDao.lockAmount(settleAccount.getId(),fee,settleAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("Update account failed,id:%s",settleAccount.getId()));
        }

        //手续费，平台Admin 出账
        AdminAccountOprLog payLog = new AdminAccountOprLog();
        payLog.setAdminAccountId(settleTask.getPayAccountId());
        payLog.setOutTradeNo(String.valueOf(settleId));
        payLog.setAmount(payAmount);
        payLog.setType(OprType.WITHDRAW.getValue());
        payLog.setGroupId(settleTask.getPayGroupId());
        payLog.setRemark(String.format("提现金额:%s,手续费率:%s,收取手续费:%s",settleTask.getSettleAmount(),feeRate,fee));

        adminAccountOprLogDao.insertSelective(payLog);

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        count = adminAccountDao.lockAmount(adminAccount.getId(),payAmount,adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update admin Account failed,%s",adminAccount.getId()));
        }

        //手续费,平台 入账手续费
        UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(settleTask.getPayGroupId());
        Account account = accountDao.selectByGroupId(subUserGroup.getId());

        AccountOprLog feeLog = new AccountOprLog();
        feeLog.setRemark(String.format("提现金额:%s,手续费率:%s,手续费:%s",settleTask.getSettleAmount(),feeRate,fee));
        feeLog.setGroupId(settleTask.getPayGroupId());
        feeLog.setType(OprType.FEE.getValue());
        feeLog.setAmount(fee);
        feeLog.setOutTradeNo(String.valueOf(settleId));
        feeLog.setAccountId(account.getId());
        accountOprLogDao.insertSelective(feeLog);

//        count = accountDao.lockAmount(account.getId(),fee,account.getVersion());
//        if(count<=0) {
//            throw new BizFailException(String.format("update account amount failed,id:%s",account.getId()));
//        }

        count = settleTaskDao.updateStatusById(settleId, SettleStatus.NEW.getValue(),SettleStatus.PROCESSING.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update settle task status faield,id:%s",settleId));
        }
    }
}
