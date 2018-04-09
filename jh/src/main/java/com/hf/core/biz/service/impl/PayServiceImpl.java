package com.hf.core.biz.service.impl;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.*;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.exceptions.RetryableException;
import com.hf.base.utils.BdUtils;
import com.hf.core.biz.service.AccountService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.*;
import com.hf.core.model.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by tengfei on 2017/10/28.
 */
@Service
public class PayServiceImpl implements PayService {
    private Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private AccountDailyLimitSumDao accountDailyLimitSumDao;
    @Autowired
    private AccountDailyLimitDao accountDailyLimitDao;
    @Autowired
    private AdminBankCardDao adminBankCardDao;
    @Autowired
    private PayRequestBackDao payRequestBackDao;

    @Override
    @Transactional
    public void saveOprLog(PayRequest payRequest) {
        Channel channel = channelDao.selectByCode(payRequest.getService(),payRequest.getChannelProviderCode());
        UserGroup userGroup = userGroupDao.selectByGroupNo(payRequest.getMchId());
        UserChannel userChannel = userChannelDao.selectByGroupChannel(userGroup.getId(),channel.getId());

        List<UserGroup> groups = getGroupChain(userGroup);
        Collections.reverse(groups);

        List<Long> groupIds = groups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
        List<Account> accounts = accountDao.selectByGroupIds(groupIds);

        if(accounts.size() != groups.size()) {
            throw new BizFailException(String.format("account group not match,%s",new Gson().toJson(groupIds)));
        }

        Map<Long,Account> groupAccountMap = accounts.parallelStream().collect(Collectors.toMap(Account::getGroupId, Function.identity()));

        List<UserChannel> userChannels = userChannelDao.selectByGroupIdList(channel.getId(),groupIds);
        if(userChannels.size() != groups.size()) {
            throw new BizFailException(String.format("userChannel group not match,%s",new Gson().toJson(groupIds)));
        }
        Map<Long,UserChannel> userChannelMap = userChannels.parallelStream().collect(Collectors.toMap(UserChannel::getGroupId,Function.identity()));

        //金额
        BigDecimal amount = new BigDecimal(payRequest.getTotalFee());
        BigDecimal standardFee = amount.multiply(channel.getFeeRate()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);

        UserGroup adminGroup = groups.get(0);

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(adminGroup.getId());

        AdminAccountOprLog adminAccountOprLog = new AdminAccountOprLog();
        adminAccountOprLog.setAdminAccountId(adminAccount.getId());
        adminAccountOprLog.setAmount(amount.subtract(standardFee));
        adminAccountOprLog.setGroupId(adminGroup.getId());
        adminAccountOprLog.setOutTradeNo(payRequest.getOutTradeNo());
        adminAccountOprLog.setRemark(String.format("管理员入账:%s,费率:%s",payRequest.getTotalFee(),channel.getFeeRate()));
        adminAccountOprLog.setType(OprType.PAY.getValue());

        adminAccountOprLogDao.insertSelective(adminAccountOprLog);

        BigDecimal totalFee = userChannel.getFeeRate().multiply(amount).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal tempFee = channel.getFeeRate().multiply(amount).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);

        Map<Long,BigDecimal> feeMap = new HashMap<>();

        for(int i=0;i<groups.size();i++) {
            UserGroup group = groups.get(i);
            if(i == groups.size()-1) {
                //最后一个
                feeMap.put(group.getId(),amount.subtract(tempFee));
                break;
            }
            BigDecimal groupFeeRate = userChannelMap.get(groups.get(i+1).getId()).getFeeRate().subtract(userChannelMap.get(group.getId()).getFeeRate());
            BigDecimal groupFee = amount.multiply(groupFeeRate).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
            groupFee = BdUtils.min(groupFee,totalFee.subtract(tempFee));
            tempFee = tempFee.add(groupFee);
            feeMap.put(group.getId(),groupFee);
        }

        List<AccountOprLog> logs = new ArrayList<>();
        for(UserGroup group:groups) {
            AccountOprLog accountOprLog = new AccountOprLog();
            logs.add(accountOprLog);
            accountOprLog.setRemark(String.format("支付金额:%s,费率:%s",payRequest.getTotalFee(),userChannelMap.get(group.getId()).getFeeRate()));
            accountOprLog.setType(OprType.PAY.getValue());
            accountOprLog.setGroupId(group.getId());
            accountOprLog.setAmount(feeMap.get(group.getId()));
            accountOprLog.setOutTradeNo(payRequest.getOutTradeNo());
            Account account = groupAccountMap.get(group.getId());
            accountOprLog.setAccountId(account.getId());
        }

        int count = accountOprLogDao.batchInsert(logs);
        if(count != logs.size()){
            throw new BizFailException("batch insert logs failed");
        }

        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        count = payRequestDao.updateActualAmount(payRequest.getId(),amount.subtract(totalFee),totalFee,payRequest.getVersion());
        if(count<=0) {
            throw new BizFailException("update pay request actualAmount failed");
        }
        count = payRequestDao.updateStatusById(payRequest.getId(), PayRequestStatus.NEW.getValue(),PayRequestStatus.OPR_GENERATED.getValue());
        if(count<=0) {
            throw new BizFailException("update pay request status failed");
        }
    }

    public List<UserGroup> getGroupChain(UserGroup leafGroup) {
        List<UserGroup> list = new ArrayList<>();
        list.add(leafGroup);
        if(leafGroup.getId().compareTo(leafGroup.getSubGroupId())==0) {
            return list;
        }
        Long tempGroupId = leafGroup.getSubGroupId();
        while(tempGroupId!=0L) {
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(tempGroupId);
            if(Objects.isNull(userGroup)) {
                break;
            }
            list.add(userGroup);
            if(userGroup.getId().compareTo(userGroup.getSubGroupId()) == 0) {
                break;
            }
            tempGroupId = userGroup.getSubGroupId();
        }
        return list;
    }

    @Transactional
    @Override
    public void paySuccess(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        if(payRequest.getStatus() != PayRequestStatus.PROCESSING.getValue()) {
            return;
        }
        int count = payRequestDao.updatePayResult(payRequest.getId(),"0",payRequest.getVersion());
        if(count<=0) {
            throw new BizFailException("update payResult failed,%s",outTradeNo);
        }
        count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.PROCESSING.getValue(),PayRequestStatus.OPR_SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update pay request status from 2 to 5 failed,%s",outTradeNo));
        }

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        count = adminAccountOprLogDao.updateStatusById(adminAccountOprLog.getId(), OprStatus.NEW.getValue(),OprStatus.PAY_SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update admin opr log status from 0 to 1 failed,log id:%s",adminAccountOprLog.getId()));
        }

        List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        for(AccountOprLog log:accountOprLogs) {
            count = accountOprLogDao.updateStatusById(log.getId(), OprStatus.NEW.getValue(),OprStatus.PAY_SUCCESS.getValue());
            if(count<=0) {
                throw new BizFailException(String.format("update opr log status from 0 to 1 failed,logid:%s",log.getId()));
            }
        }
    }

    @Transactional
    @Override
    public void payFailed(String outTradeNo, PayMsgRecord hfResultMsg) {
        payFailed(outTradeNo);
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_SUCCESS.getValue(),PayRequestStatus.PAY_FAILED.getValue());
        if(count<=0) {
            throw new BizFailException("update status failed");
        }
    }

    @Transactional
    @Override
    public void payFailed(String outTradeNo, PayRequestBack payRequestBack) {
        payFailed(outTradeNo);
        payRequestBackDao.insertSelective(payRequestBack);
    }

    @Transactional
    @Override
    public void payFailed(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        if(payRequest.getStatus() != PayRequestStatus.OPR_GENERATED.getValue() && payRequest.getStatus() != PayRequestStatus.PROCESSING.getValue()) {
            throw new BizFailException(String.format("status:%s,not valid,%s",payRequest.getStatus(),outTradeNo));
        }
        int count = payRequestDao.updateStatusById(payRequest.getId(),payRequest.getStatus(),PayRequestStatus.OPR_SUCCESS.getValue());
        if(count <=0) {
            logger.warn(String.format("update payRequest status failed,%s",outTradeNo));
            throw new BizFailException(String.format("update payRequest status failed,%s",outTradeNo));
        }
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        count = payRequestDao.updatePayResult(payRequest.getId(),"1",payRequest.getVersion());
        if(count<=0) {
            throw new BizFailException("update pay result failed,%s",outTradeNo);
        }
        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        count = adminAccountOprLogDao.updateStatusById(adminAccountOprLog.getId(), OprStatus.NEW.getValue(),OprStatus.PAY_FAILED.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update admin opr log status from 0 to 99 failed,log id:%s",adminAccountOprLog.getId()));
        }

        List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByTradeNo(outTradeNo);
        for(AccountOprLog log:accountOprLogs) {
            count = accountOprLogDao.updateStatusById(log.getId(), OprStatus.NEW.getValue(),OprStatus.PAY_FAILED.getValue());
            if(count<=0) {
                throw new BizFailException(String.format("update opr log status from 0 to 99 failed,logid:%s",log.getId()));
            }
        }
    }

    @Transactional
    @Override
    public void payPromote(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.USER_NOTIFIED.getValue(),PayRequestStatus.PAY_SUCCESS.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update payrequest status from 5 to 10 failed,tradeNo:%s",outTradeNo));
        }

        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(outTradeNo);
        count = adminAccountOprLogDao.updateStatusById(adminAccountOprLog.getId(),OprStatus.PAY_SUCCESS.getValue(),OprStatus.FINISHED.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update account opr status failed,adminLogId:%s",adminAccountOprLog.getId()));
        }

        AdminAccount adminAccount = adminAccountDao.selectByPrimaryKey(adminAccountOprLog.getAdminAccountId());
        count = adminAccountDao.addAmount(adminAccount.getId(),adminAccountOprLog.getAmount(),adminAccount.getVersion());
        if(count<=0) {
            throw new BizFailException(String.format("update admin Account amount failed,id:%s,amount:%s",adminAccount.getId(),adminAccountOprLog.getAmount()));
        }

        List<AccountOprLog> logs = accountOprLogDao.selectByTradeNo(outTradeNo);
        logs.stream().forEach(log -> accountService.promote(log));
    }

    @Transactional
    @Override
    public void savePayMsg(List<PayMsgRecord> records) {
        for(PayMsgRecord payMsgRecord : records) {
            payMsgRecordDao.insertSelective(payMsgRecord);
        }
    }

    @Transactional
    @Override
    public synchronized Map<Long,BigDecimal> chooseAdminBank(Long groupId,BigDecimal amount) {
        AccountDailyLimitSum accountDailyLimitSum = accountDailyLimitSumDao.selectByGroupId(groupId);
        if(Objects.isNull(accountDailyLimitSum)) {
            throw new BizFailException();
        }
        int count = accountDailyLimitSumDao.lock(accountDailyLimitSum.getId(),amount,accountDailyLimitSum.getVersion());
        if(count <= 0) {
            throw new BizFailException();
        }

        List<AccountDailyLimit> list = accountDailyLimitDao.selectByGroupId(groupId);
        if(CollectionUtils.isEmpty(list)) {
            throw new BizFailException();
        }

        Map<Long,BigDecimal> map = new HashMap<>();

        BigDecimal tempAmount = amount;
        for(AccountDailyLimit accountDailyLimit:list) {
            BigDecimal avaAmount = accountDailyLimit.getLimitAmount().subtract(accountDailyLimit.getLockAmount());
            BigDecimal curAmount = BdUtils.min(avaAmount,tempAmount);

            map.put(accountDailyLimit.getRefId(),curAmount);

            count = accountDailyLimitDao.lock(accountDailyLimit.getId(),curAmount,accountDailyLimit.getVersion());
            if(count<=0) {
                throw new BizFailException();
            }

            tempAmount = tempAmount.subtract(curAmount);
            if(tempAmount.compareTo(BigDecimal.ZERO)<=0) {
                break;
            }
        }

        if(tempAmount.compareTo(BigDecimal.ZERO)!=0) {
            throw new BizFailException();
        }

        return map;
    }

    @Transactional
    @Retryable(value= {RetryableException.class},maxAttempts = 3,backoff = @Backoff(delay = 100,multiplier = 1))
    @Override
    public synchronized AdminBankCard chooseAdminBank1(Long groupId, BigDecimal amount) {
        AccountDailyLimit accountDailyLimit = accountDailyLimitDao.selectAvailableAccount(groupId,amount);
        if(Objects.isNull(accountDailyLimit)) {
            throw new BizFailException(CodeManager.PAY_FAILED,"额度不足");
        }

        int count = accountDailyLimitDao.lock(accountDailyLimit.getId(),amount,accountDailyLimit.getVersion());
        if(count<=0) {
            throw new RetryableException();
        }

        AccountDailyLimitSum accountDailyLimitSum = accountDailyLimitSumDao.selectByGroupId(groupId);
        if(Objects.isNull(accountDailyLimitSum)) {
            throw new BizFailException(CodeManager.PAY_FAILED,"额度不足");
        }
        count = accountDailyLimitSumDao.lock(accountDailyLimitSum.getId(),amount,accountDailyLimitSum.getVersion());
        if(count <= 0) {
            throw new RetryableException();
        }

        return adminBankCardDao.selectByPrimaryKey(accountDailyLimit.getRefId());
    }

    @Transactional
    @Override
    public void savePayRequest(List<PayMsgRecord> msgRecords, PayRequest payRequest) {
        for(PayMsgRecord payMsgRecord : msgRecords) {
            payMsgRecordDao.insertSelective(payMsgRecord);
        }
        payRequestDao.insertSelective(payRequest);
    }

    @Transactional
    @Override
    public void updateDailyLimit() {
        List<AdminBankCard> list = adminBankCardDao.select(new HashMap<>());
        Map<Long,List<AdminBankCard>> map = list.parallelStream().collect(Collectors.groupingBy(AdminBankCard::getGroupId));
        for(Long groupId:map.keySet()) {
            for(AdminBankCard adminBankCard:list) {
                AccountDailyLimit accountDailyLimit = new AccountDailyLimit();
                accountDailyLimit.setGroupId(adminBankCard.getGroupId());
                accountDailyLimit.setLimitAmount(adminBankCard.getLimitAmount());
                accountDailyLimit.setRefId(adminBankCard.getId());
                accountDailyLimit.setDateStr(new SimpleDateFormat("yyyyMMdd").format(new Date()));
                try {
                    accountDailyLimitDao.insertSelective(accountDailyLimit);
                } catch (DuplicateKeyException e) {
                    logger.warn(e.getMessage());
                }
            }

            List<AdminBankCard> cards = map.get(groupId);
            AccountDailyLimitSum accountDailyLimitSum = new AccountDailyLimitSum();
            accountDailyLimitSum.setDateStr(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            accountDailyLimitSum.setGroupId(groupId);
            accountDailyLimitSum.setSumLimitAmount(cards.parallelStream().map(AdminBankCard::getLimitAmount).reduce(new BigDecimal("0"),BigDecimal::add));
            try {
                accountDailyLimitSumDao.insertSelective(accountDailyLimitSum);
            } catch (DuplicateKeyException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    public void savePayMsg(PayMsgRecord payMsgRecord) {
        try {
            payMsgRecordDao.insertSelective(payMsgRecord);
        } catch (DuplicateKeyException e) {
            logger.warn(String.format("msg already exist,outTradeNo:%s,tradeType:%s,operateType:%s",payMsgRecord.getOutTradeNo(),payMsgRecord.getTradeType(),payMsgRecord.getOperateType()));
        }
    }

    @Transactional
    @Override
    public void remoteSuccess(PayRequest payRequest, PayRequestBack payRequestBack) {
        int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_GENERATED.getValue(),PayRequestStatus.PROCESSING.getValue());
        if(count<=0) {
            throw new BizFailException("update pay request status failed,%s",payRequest.getOutTradeNo());
        }
        payRequestBackDao.insertSelective(payRequestBack);
    }

    @Transactional
    @Override
    public void remoteSuccess(PayRequest payRequest,PayMsgRecord hfResultMsg) {
        int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_GENERATED.getValue(),PayRequestStatus.PROCESSING.getValue());
        if(count<=0) {
            throw new BizFailException("update pay request status failed,%s",payRequest.getOutTradeNo());
        }
//        payMsgRecordDao.insertSelective(hfResultMsg);
    }

    @Transactional
    @Override
    public void startRefund(PayRequest payRequest, PayMsgRecord payMsgRecord) {
        int count = 0;
        if(PayRequestStatus.USER_NOTIFIED == PayRequestStatus.parse(payRequest.getStatus())) {
            count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.USER_NOTIFIED.getValue(),PayRequestStatus.REFUNDING.getValue());
        } else if(PayRequestStatus.OPR_SUCCESS == PayRequestStatus.parse(payRequest.getStatus())) {
            count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_SUCCESS.getValue(),PayRequestStatus.REFUNDING.getValue());
        }
        if(count<=0) {
            throw new BizFailException("update payrequest refunding failed");
        }
        payMsgRecordDao.insertSelective(payMsgRecord);
    }

    @Transactional
    @Override
    public void finishRefund(PayRequest payRequest) {
        payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
        if(payRequest.getStatus() == PayRequestStatus.REFUNDED.getValue()) {
            return;
        }

        int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.REFUNDING.getValue(),PayRequestStatus.REFUNDED.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update status to refuned failed,%s",payRequest.getOutTradeNo()));
        }
        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(payRequest.getOutTradeNo());
        count = adminAccountOprLogDao.updateStatusById(adminAccountOprLog.getId(),OprStatus.PAY_SUCCESS.getValue(),OprStatus.REFUND.getValue());
        if(count<=0) {
            throw new BizFailException(String.format("update admin account opr log status failed,%s",payRequest.getOutTradeNo()));
        }

        List<AccountOprLog> logs = accountOprLogDao.selectByTradeNo(payRequest.getOutTradeNo());
        for(AccountOprLog log:logs) {
            count = accountOprLogDao.updateStatusById(log.getId(),OprStatus.PAY_SUCCESS.getValue(),OprStatus.REFUND.getValue());
            if(count<=0) {
                throw new BizFailException(String.format("update opr log status failed,%s",payRequest.getOutTradeNo()));
            }
        }
    }
}
