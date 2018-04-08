package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.AccountPageInfo;
import com.hf.base.model.AccountRequest;
import com.hf.base.utils.Pagenation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AdminAccountDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCurrentPage(1);
        if(StringUtils.isNotEmpty(request.getParameter("currentPage"))) {
            accountRequest.setCurrentPage(Integer.parseInt(request.getParameter("currentPage")));
        }
        accountRequest.setPageSize(15);
        accountRequest.setGroupId(Long.parseLong(groupId));
        Pagenation<AccountPageInfo> pagenation = defaultClient.getAccountList(accountRequest);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("pageInfo",pagenation);
        dispatchResult.addObject("requestInfo",accountRequest);

        return dispatchResult;
    }
}
