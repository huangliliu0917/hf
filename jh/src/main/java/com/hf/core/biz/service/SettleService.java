package com.hf.core.biz.service;

import com.hf.base.model.AgentPayLog;
import com.hf.core.model.po.AccountOprLog;
import com.hf.core.model.po.SettleTask;

import java.util.List;

public interface SettleService {
    void agentPay(SettleTask settleTask, List<AgentPayLog> logs);
    void agentPaySuccess(SettleTask settleTask, AccountOprLog accountOprLog);
    void agentPayFailed(Long logId);
    void paySuccess(SettleTask settleTask, AccountOprLog accountOprLog);
    void adminPaySuccess(SettleTask settleTask, AccountOprLog accountOprLog);
}
