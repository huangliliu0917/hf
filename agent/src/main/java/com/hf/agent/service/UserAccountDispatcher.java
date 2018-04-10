package com.hf.agent.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Service
public class UserAccountDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        Account account = defaultClient.getAccountByGroupId(groupId);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);

        BigDecimal finishAmount = defaultClient.getSumFinishAmount(groupId);
        if(null == finishAmount) {
            dispatchResult.addObject("todayAmount",0);
        } else {
            dispatchResult.addObject("todayAmount",finishAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        }

        dispatchResult.addObject("totalAmount",(account.getAmount().add(account.getPaidAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));

        dispatchResult.addObject("amount",(account.getAmount().subtract(account.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));

        dispatchResult.addObject("lockAmount",account.getLockAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        dispatchResult.addObject("paidAmount",account.getPaidAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));

        return dispatchResult;
    }
}
