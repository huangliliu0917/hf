package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Service
public class UserAccountInfoDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        Account account = defaultClient.getAccountByGroupId(groupId);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("amount",(account.getAmount().subtract(account.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        dispatchResult.addObject("lockAmount",account.getLockAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        dispatchResult.addObject("paidAmount",account.getPaidAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        dispatchResult.addObject("fee",account.getFee().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        BigDecimal sumLockAmount = defaultClient.getSumLockAmount(groupId);
        if(null== sumLockAmount) {
            dispatchResult.addObject("logAmount",0);
        } else {
            dispatchResult.addObject("logAmount",sumLockAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        }
        return dispatchResult;
    }
}
