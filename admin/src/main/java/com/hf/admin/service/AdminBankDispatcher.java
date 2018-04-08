package com.hf.admin.service;

import com.hf.admin.rpc.AdminClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.AdminBankCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminBankDispatcher implements Dispatcher {
    @Autowired
    private AdminClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        String channelNo = request.getParameter("channelNo");
        List<AdminBankCard> list = client.getAdminBankCardList(groupId,channelNo);
        list.stream().forEach(adminBankCard -> {
            adminBankCard.setLimitAmount(adminBankCard.getLimitAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        });
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("cards",list);
        dispatchResult.addObject("channelNo",channelNo);
        return dispatchResult;
    }
}
