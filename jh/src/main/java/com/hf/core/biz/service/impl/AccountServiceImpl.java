package com.hf.core.biz.service.impl;

import com.hf.base.enums.OprStatus;
import com.hf.base.enums.OprType;
import com.hf.base.enums.SettleStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.AccountService;
import com.hf.core.dao.local.*;
import com.hf.core.model.dto.RefundResponse;
import com.hf.core.model.dto.ReverseResponse;
import com.hf.core.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
    @Autowired
    private UserChannelAccountDao userChannelAccountDao;

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
        UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(log.getGroupId(),log.getProviderCode());
        count = userChannelAccountDao.addAmount(userChannelAccount.getId(),log.getAmount(),userChannelAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update user channel account amount failed.opr id:%s",log.getId()));
        }
    }

    @Transactional
    @Override
    public void settle(SettleTask settleTask) {
        settleTaskDao.insertSelective(settleTask);
        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        BigDecimal payAmount = settleTask.getPayAmount();
        BigDecimal feeRate = settleTask.getFeeRate();
        BigDecimal fee = settleTask.getFee();

        Account settleAccount = accountDao.selectByGroupId(settleTask.getGroupId());
        List<UserChannelAccount> userChannelAccounts = userChannelAccountDao.getBidding(settleAccount.getGroupId(),settleTask.getSettleAmount());

        for(UserChannelAccount userChannelAccount:userChannelAccounts) {
            if(payAmount.compareTo(BigDecimal.ZERO)<=0 && fee.compareTo(BigDecimal.ZERO)<=0) {
                break;
            }
            BigDecimal userChannelAmount = userChannelAccount.getAmount().subtract(userChannelAccount.getLockAmount());

            if(payAmount.compareTo(BigDecimal.ZERO)>0) {
                BigDecimal currentAmount = Utils.min(userChannelAmount,payAmount);
                payAmount = payAmount.subtract(currentAmount);
                userChannelAmount = userChannelAmount.subtract(currentAmount);

                if(currentAmount.compareTo(BigDecimal.ZERO)<=0) {
                    continue;
                }

                AccountOprLog log = new AccountOprLog();
                log.setRemark(String.format("提现金额:%s,手续费率:%s,转账金额:%s",settleTask.getSettleAmount(),feeRate,fee));
                log.setGroupId(settleTask.getGroupId());
                log.setType(OprType.WITHDRAW.getValue());
                log.setAmount(currentAmount);
                log.setOutTradeNo(String.valueOf(settleTask.getId()));
                log.setAccountId(settleTask.getGroupId());
                log.setProviderCode(userChannelAccount.getChannelProvider());

                accountOprLogDao.insertSelective(log);

                settleAccount = accountDao.selectByGroupId(settleTask.getGroupId());

                int count = accountDao.lockAmount(settleAccount.getId(),currentAmount,settleAccount.getVersion());
                if(count<=0) {
                    throw new BizFailException(String.format("update account failed,id:%s",settleAccount.getId()));
                }

                count = userChannelAccountDao.lockAmount(userChannelAccount.getId(),log.getAmount(),userChannelAccount.getVersion());
                if(count<=0) {
                    throw new BizFailException("update user channel account failed");
                }
            }

            if(fee.compareTo(BigDecimal.ZERO)>0) {
                if(userChannelAmount.compareTo(BigDecimal.ZERO)<=0) {
                    continue;
                }
                BigDecimal currentAmount = Utils.min(userChannelAmount,fee);

                fee = fee.subtract(currentAmount);
                userChannelAmount = userChannelAmount.subtract(currentAmount);

                settleAccount = accountDao.selectByPrimaryKey(settleAccount.getId());
                //手续费，提现方
                AccountOprLog taxLog = new AccountOprLog();
                taxLog.setRemark(String.format("提现金额:%s,手续费率:%s,手续费:%s",settleTask.getSettleAmount(),feeRate,fee));
                taxLog.setGroupId(settleTask.getGroupId());
                taxLog.setType(OprType.TAX.getValue());
                taxLog.setAmount(currentAmount);
                taxLog.setOutTradeNo(String.valueOf(settleTask.getId()));
                taxLog.setAccountId(settleTask.getAccountId());
                taxLog.setProviderCode(userChannelAccount.getChannelProvider());

                accountOprLogDao.insertSelective(taxLog);
                settleAccount = accountDao.selectByPrimaryKey(settleAccount.getId());
                int count = accountDao.lockAmount(settleAccount.getId(),currentAmount,settleAccount.getVersion());
                if(count<=0) {
                    throw new BizFailException(String.format("Update account failed,id:%s",settleAccount.getId()));
                }

                //手续费,平台 入账手续费
                UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(settleTask.getPayGroupId());
                Account account = accountDao.selectByGroupId(subUserGroup.getId());

                AccountOprLog feeLog = new AccountOprLog();
                feeLog.setRemark(String.format("提现金额:%s,手续费率:%s,手续费:%s",settleTask.getSettleAmount(),feeRate,fee));
                feeLog.setGroupId(settleTask.getPayGroupId());
                feeLog.setType(OprType.FEE.getValue());
                feeLog.setAmount(currentAmount);
                feeLog.setOutTradeNo(String.valueOf(settleTask.getId()));
                feeLog.setAccountId(account.getId());
                feeLog.setProviderCode(userChannelAccount.getChannelProvider());
                accountOprLogDao.insertSelective(feeLog);

                userChannelAccount = userChannelAccountDao.selectByPrimaryKey(userChannelAccount.getId());
                count = userChannelAccountDao.lockAmount(userChannelAccount.getId(),feeLog.getAmount(),userChannelAccount.getVersion());
                if(count<=0) {
                    throw new BizFailException("update user channel account failed");
                }
            }
        }

        if(fee.compareTo(BigDecimal.ZERO)>0 || payAmount.compareTo(BigDecimal.ZERO)>0) {
            throw new BizFailException("金额不足");
        }

        AdminAccountOprLog payLog = new AdminAccountOprLog();
        payLog.setAdminAccountId(settleTask.getPayAccountId());
        payLog.setOutTradeNo(String.valueOf(settleTask.getId()));
        payLog.setAmount(settleTask.getPayAmount());
        payLog.setType(OprType.WITHDRAW.getValue());
        payLog.setGroupId(settleTask.getPayGroupId());
        payLog.setRemark(String.format("提现金额:%s,手续费率:%s,收取手续费:%s",settleTask.getSettleAmount(),feeRate,fee));

        adminAccountOprLogDao.insertSelective(payLog);

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        int count = adminAccountDao.lockAmount(adminAccount.getId(),settleTask.getPayAmount(),adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update admin Account failed,%s",adminAccount.getId()));
        }

        count = settleTaskDao.updateStatusById(settleTask.getId(), SettleStatus.NEW.getValue(),SettleStatus.PROCESSING.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update settle task status faield,id:%s",settleTask.getId()));
        }
    }

    @Transactional
    @Override
    public void adminSettle(SettleTask settleTask) {
        settleTaskDao.insertSelective(settleTask);
        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());

        BigDecimal amount = settleTask.getPayAmount().subtract(settleTask.getLockAmount());

        Long groupId = settleTask.getGroupId();
        //admin account
        AdminAccountOprLog adminAccountOprLog = new AdminAccountOprLog();
        adminAccountOprLog.setAdminAccountId(settleTask.getPayAccountId());
        adminAccountOprLog.setOutTradeNo(String.valueOf(settleTask.getId()));
        adminAccountOprLog.setAmount(settleTask.getPayAmount());
        adminAccountOprLog.setType(OprType.WITHDRAW.getValue());
        adminAccountOprLog.setGroupId(settleTask.getPayGroupId());
        adminAccountOprLog.setRemark(String.format("提现金额:%s",settleTask.getSettleAmount()));
        adminAccountOprLogDao.insertSelective(adminAccountOprLog);

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        int count = adminAccountDao.lockAmount(adminAccount.getId(),settleTask.getPayAmount(),adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update admin Account failed,%s",adminAccount.getId()));
        }

        //pay group
        BigDecimal tempAmount = settleTask.getPayAmount();
        //pay account
        Account account = accountDao.selectByGroupId(groupId);
        List<UserChannelAccount> userChannelAccounts = userChannelAccountDao.getBidding(groupId,account.getAmount().subtract(account.getLockAmount()));

        for(UserChannelAccount userChannelAccount:userChannelAccounts) {
            if(tempAmount.compareTo(BigDecimal.ZERO)<=0) {
                break;
            }
            if(userChannelAccount.getChannelProvider().equals("jh")) {
                continue;
            }

            BigDecimal channelAmount = userChannelAccount.getAmount().subtract(userChannelAccount.getLockAmount());
            BigDecimal currentAmount = Utils.min(tempAmount,channelAmount);
            tempAmount = tempAmount.subtract(currentAmount);

            AccountOprLog log = new AccountOprLog();
            log.setRemark(String.format("提现金额:%s",currentAmount));
            log.setGroupId(settleTask.getGroupId());
            log.setType(OprType.WITHDRAW.getValue());
            log.setAmount(currentAmount);
            log.setOutTradeNo(String.valueOf(settleTask.getId()));
            log.setAccountId(settleTask.getGroupId());
            log.setProviderCode(userChannelAccount.getChannelProvider());

            accountOprLogDao.insertSelective(log);

            account = accountDao.selectByGroupId(settleTask.getGroupId());

            count = accountDao.lockAmount(account.getId(),currentAmount,account.getVersion());
            if(count<=0) {
                throw new BizFailException(String.format("update account failed,id:%s",account.getId()));
            }

            count = userChannelAccountDao.lockAmount(userChannelAccount.getId(),log.getAmount(),userChannelAccount.getVersion());
            if(count<=0) {
                throw new BizFailException("update user channel account failed");
            }
        }

        List<Account> accounts = accountDao.selectAvaAccount();
        for(Account customerAccount:accounts) {
            if(tempAmount.compareTo(BigDecimal.ZERO)<=0) {
                break;
            }
            List<UserChannelAccount> customerUserChannelAccounts = userChannelAccountDao.getBidding(customerAccount.getGroupId(),tempAmount);

            for(UserChannelAccount customerUserChannelAccount:customerUserChannelAccounts) {
                if(tempAmount.compareTo(BigDecimal.ZERO)<=0) {
                    break;
                }
                BigDecimal channelAmount = customerUserChannelAccount.getAmount().subtract(customerUserChannelAccount.getLockAmount());
                BigDecimal currentAmount = Utils.min(channelAmount,tempAmount);
                tempAmount = tempAmount.subtract(currentAmount);

                AccountOprLog log = new AccountOprLog();
                log.setRemark(String.format("提现金额:%s",currentAmount));
                //管理员id
                log.setGroupId(settleTask.getGroupId());
                log.setType(OprType.ADMIN_WITHDRAW.getValue());
                log.setAmount(currentAmount);
                log.setOutTradeNo(String.valueOf(settleTask.getId()));
                //付款人id
                log.setAccountId(customerUserChannelAccount.getGroupId());
                log.setProviderCode(customerUserChannelAccount.getChannelProvider());

                accountOprLogDao.insertSelective(log);

                count = userChannelAccountDao.lockAmount(customerUserChannelAccount.getId(),currentAmount,customerUserChannelAccount.getVersion());
                if(count<=0) {
                    throw new BizFailException("lock user channel account failed");
                }
            }
        }

        if(tempAmount.compareTo(BigDecimal.ZERO)>0) {
            throw new BizFailException("金额不足");
        }

        count = settleTaskDao.updateStatusById(settleTask.getId(), SettleStatus.NEW.getValue(),SettleStatus.PROCESSING.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update settle task status faield,id:%s",settleTask.getId()));
        }
    }
}
