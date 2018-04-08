package com.hf.agent.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserGroup;
import com.hf.base.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AgentPersonalDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        UserGroup userGroup = defaultClient.getUserGroupById(groupId);
        Long userId = Long.parseLong(request.getSession().getAttribute("userId").toString());
        UserInfo userInfo = defaultClient.getUserInfoById(userId);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("userInfo",userInfo);
        dispatchResult.addObject("userGroup",userGroup);

        return dispatchResult;
    }
}
