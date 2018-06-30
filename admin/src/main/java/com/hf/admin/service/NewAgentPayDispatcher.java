package com.hf.admin.service;

import com.hf.admin.rpc.AdminClient;
import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.model.AgentPayLog;
import com.hf.base.model.SettleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewAgentPayDispatcher implements Dispatcher {
    @Autowired
    private AdminClient adminClient;
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String withdrawId = request.getParameter("taskId");
        String groupId = request.getSession().getAttribute("groupId").toString();
        SettleTask settleTask = defaultClient.getSettleTask(withdrawId);

        List<AgentPayLog> agentPayLogs = adminClient.newAgentPay(withdrawId);
        BigDecimal totalAmount = agentPayLogs.stream().map(AgentPayLog::getAmount).reduce(new BigDecimal("0"),BigDecimal::add);

        Map<String,Object> result = new HashMap<>();
        result.put("residueAmount",(settleTask.getPayAmount().subtract(settleTask.getPaidAmount()).subtract(settleTask.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        result.put("lockAmount",totalAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        result.put("taskId",settleTask.getId());
        result.put("datas",agentPayLogs);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.setData(result);
        return dispatchResult;
    }
}
