package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.WithDrawInfo;
import com.hf.base.model.WithDrawRequest;
import com.hf.base.utils.Pagenation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserWithDrawRecordDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        Integer currentPage = 1;
        if(StringUtils.isNotEmpty(request.getParameter("currentPage"))) {
            currentPage = Integer.parseInt(request.getParameter("currentPage"));
        }
        WithDrawRequest withDrawRequest = new WithDrawRequest();
        withDrawRequest.setGroupId(groupId);
        withDrawRequest.setCurrentPage(currentPage);
        withDrawRequest.setPageSize(15);
        String status = request.getParameter("status");
        if(StringUtils.isNotEmpty(status)) {
            withDrawRequest.setStatus(Integer.parseInt(status));
        }

        Pagenation<WithDrawInfo> pagenation = client.getWithDrawPage(withDrawRequest);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("pageInfo",pagenation);
        dispatchResult.addObject("requestInfo",withDrawRequest);

        return dispatchResult;
    }

}
