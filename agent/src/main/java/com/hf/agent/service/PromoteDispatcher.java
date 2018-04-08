package com.hf.agent.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class PromoteDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String userId = request.getSession().getAttribute("userId").toString();
        UserInfo userInfo = client.getUserInfoById(Long.parseLong(userId));

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("inviteCode",userInfo.getInviteCode());
        return dispatchResult;
    }
}
