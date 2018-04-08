package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.AccountRequest;
import com.hf.base.model.AdminAccountPageInfo;
import com.hf.base.utils.Pagenation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AdminAccountAdminDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setGroupId(groupId);
        accountRequest.setCurrentPage(1);
        accountRequest.setPageSize(15);
        Pagenation<AdminAccountPageInfo> pagenation = client.getAdminAccountList(accountRequest);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("pageInfo",pagenation);
        dispatchResult.addObject("requestInfo",accountRequest);
        return dispatchResult;
    }
}
