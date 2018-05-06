package com.hf.admin.service;

import com.hf.admin.rpc.AdminClient;
import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.Account;
import com.hf.base.model.AdminAccount;
import com.hf.base.model.UserBankCard;
import com.hf.base.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class AdminWithdrawalDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        AdminAccount adminAccount = client.getAdminAccountByGroupId(groupId);
        BigDecimal amount = (adminAccount.getAmount().subtract(adminAccount.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal lockAmount = adminAccount.getLockAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal paidAmount = adminAccount.getPaidAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);

        Map<String,Object> accountInfo = MapUtils.buildMap("amount",amount,"lockAmount",lockAmount,"paidAmount",paidAmount);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("accountInfo",accountInfo);
        return dispatchResult;
    }
}
