package com.hf.core.biz.service.impl;

import com.hf.base.enums.SettleStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.biz.service.SettleService;
import com.hf.core.dao.local.AgentPayLogDao;
import com.hf.core.dao.local.SettleTaskDao;
import com.hf.core.dao.local.UserChannelAccountDao;
import com.hf.core.model.po.AgentPayLog;
import com.hf.core.model.po.SettleTask;
import com.hf.core.model.po.UserChannelAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SettleServiceImpl implements SettleService {
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private AgentPayLogDao agentPayLogDao;
    @Autowired
    private UserChannelAccountDao userChannelAccountDao;

    @Transactional
    @Override
    public void agentPay(SettleTask settleTask, List<AgentPayLog> logs) {
        BigDecimal totalAmount = logs.stream().map(AgentPayLog::getAmount).reduce(new BigDecimal("0"),BigDecimal::add);

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());

        int count = settleTaskDao.lockAmount(settleTask.getId(),totalAmount,settleTask.getVersion());
        if(count<=0) {
            throw new BizFailException("update settle task amount failed");
        }

        for(AgentPayLog agentPayLog:logs) {
            agentPayLogDao.insertSelective(agentPayLog);
            UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(settleTask.getGroupId(),agentPayLog.getProviderCode());
            userChannelAccountDao.lockAmount(userChannelAccount.getId(),agentPayLog.getAmount(),userChannelAccount.getVersion());
        }
    }

    @Override
    public void agentPaySuccess() {

    }

    @Transactional
    @Override
    public void agentPayFailed(Long logId) {
        AgentPayLog agentPayLog = agentPayLogDao.selectByPrimaryKey(logId);
        int count = agentPayLogDao.updateStatus(agentPayLog.getId(), SettleStatus.NEW.getValue(),SettleStatus.FAILED.getValue());
        if(count<=0) {
            throw new BizFailException("update agent pay log status failed");
        }
        SettleTask settleTask = settleTaskDao.selectByPrimaryKey(agentPayLog.getWithDrawTaskId());
        count = settleTaskDao.unlock(settleTask.getId(),agentPayLog.getAmount(),settleTask.getVersion());
        if(count<=0) {
            throw new BizFailException("unlock task failed");
        }
        UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(settleTask.getGroupId(),agentPayLog.getProviderCode());
        count = userChannelAccountDao.unlock(userChannelAccount.getId(),agentPayLog.getAmount(),userChannelAccount.getVersion());
        if(count<=0) {
            throw new BizFailException("unlock account failed");
        }
    }
}
