package com.hf.agent.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserGroupDto;
import com.hf.base.utils.Pagenation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

@Service
public class AgentMemberDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        Pagenation<UserGroupDto> pagenation = client.getUserGroupOfAgent(groupId,null,null,null);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        return dispatchResult;
    }
}
