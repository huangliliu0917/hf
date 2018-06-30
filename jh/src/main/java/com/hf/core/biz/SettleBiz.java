package com.hf.core.biz;

import com.hf.base.model.AgentPayLog;
import com.hf.base.model.WithDrawInfo;
import com.hf.base.model.WithDrawRequest;
import com.hf.base.utils.Pagenation;
import com.hf.core.model.po.SettleTask;

import java.util.List;

public interface SettleBiz {
    void saveSettle(SettleTask settleTask);
    void finishSettle(Long id);
    void settleFailed(Long id);
    Pagenation<WithDrawInfo> getWithDrawPage(WithDrawRequest withDrawRequest);
    List<AgentPayLog> newAgentPay(String withDrawId);
    void submitAgentPay(Long id);
    void finishAgentPay(Long id);
}
