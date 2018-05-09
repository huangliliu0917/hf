package com.hf.agent.service;

import com.hf.base.client.AdminClient;
import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.SubGroupRequest;
import com.hf.base.model.UserGroup;
import com.hf.base.model.UserGroupDto;
import com.hf.base.model.UserGroupRequest;
import com.hf.base.utils.Pagenation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

@Service
public class AgentMemberDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;
    @Autowired
    private AdminClient adminClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        SubGroupRequest subGroupRequest = new SubGroupRequest();
        subGroupRequest.setId(Long.parseLong(groupId));
        subGroupRequest.setPageIndex(1);
        subGroupRequest.setPageSize(Integer.MAX_VALUE);

        Pagenation<UserGroupDto> pagenation = client.getUserGroupOfAgent(subGroupRequest);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("pagenation",pagenation);
        return dispatchResult;
    }
}
