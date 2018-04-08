package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.Account;
import com.hf.base.model.AdminAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Service
public class DashboardDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        Account account = defaultClient.getAccountByGroupId(groupId);
        AdminAccount adminAccount = defaultClient.getAdminAccountByGroupId(groupId);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.addObject("amount",(adminAccount.getAmount().subtract(adminAccount.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        dispatchResult.addObject("lockAmount",adminAccount.getLockAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));

        BigDecimal sumLockAmount = defaultClient.getSumLockAmount(groupId);
        if(null == sumLockAmount) {
            sumLockAmount = new BigDecimal("0");
        }
        dispatchResult.addObject("sumLockAmount",sumLockAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        dispatchResult.addObject("paidAmount",adminAccount.getPaidAmount().divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP));
        dispatchResult.addObject("totalIncome",(account.getAmount().subtract(account.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));

        dispatchResult.setPage(page);
        return dispatchResult;
    }
}
