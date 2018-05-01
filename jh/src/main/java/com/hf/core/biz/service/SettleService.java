package com.hf.core.biz.service;

import com.hf.core.model.po.AgentPayLog;
import com.hf.core.model.po.SettleTask;

import java.util.List;

public interface SettleService {
    void agentPay(SettleTask settleTask, List<AgentPayLog> logs);
    void agentPaySuccess();
    void agentPayFailed(Long logId);
}
