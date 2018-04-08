package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserBankCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserBankCardDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getParameter("id"));
        List<UserBankCard> list = client.getUserBankCard(groupId);
        DispatchResult result = new DispatchResult();
        result.setPage(page);
        result.addObject("cards",list);
        result.addObject("group",groupId);
        return result;
    }
}
