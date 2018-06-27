package com.hf.admin.service;

import com.hf.admin.model.UserGroupRequest;
import com.hf.admin.rpc.AdminClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserGroup;
import com.hf.base.utils.Pagenation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class GroupIndexDispatcher implements Dispatcher {
    @Autowired
    private AdminClient adminClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String companyId = request.getSession().getAttribute("groupId").toString();
        UserGroupRequest userGroupRequest = new UserGroupRequest();
        userGroupRequest.setCompanyId(companyId);
        userGroupRequest.setPageSize(15);

        StringBuilder urlParams = new StringBuilder();

        if(StringUtils.isNotEmpty(request.getParameter("currentPage"))) {
            userGroupRequest.setPageIndex(Integer.parseInt(request.getParameter("currentPage")));
        }

        if(StringUtils.isNotEmpty(request.getParameter("user"))) {
            userGroupRequest.setUser(request.getParameter("user"));
            urlParams = urlParams.append("user=").append(request.getParameter("user"));
        }

        if(StringUtils.isNotEmpty(request.getParameter("agent"))) {
            userGroupRequest.setAgent(request.getParameter("agent"));
            urlParams = urlParams.append("&").append("agent=").append(request.getParameter("agent"));
        }

        if(StringUtils.isNotEmpty(request.getParameter("status"))) {
            userGroupRequest.setStatus(Integer.parseInt(request.getParameter("status")));
            urlParams = urlParams.append("&").append("status=").append(request.getParameter("status"));
        }

        if(StringUtils.isNotEmpty(request.getParameter("type"))) {
            userGroupRequest.setType(Integer.parseInt(request.getParameter("type")));
            urlParams = urlParams.append("&").append("type=").append(request.getParameter("type"));
        }

        Pagenation<UserGroup> pagenation = adminClient.getUserGroupList(userGroupRequest);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("pageInfo",pagenation);


        dispatchResult.addObject("urlParams",urlParams.toString());
        return dispatchResult;
    }
}
