package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.WithDrawRole;
import com.hf.base.model.WithDrawInfo;
import com.hf.base.model.WithDrawRequest;
import com.hf.base.utils.Pagenation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AdminWithDrawDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());

        WithDrawRequest withDrawRequest = new WithDrawRequest();
        withDrawRequest.setGroupId(groupId);
        withDrawRequest.setPageSize(15);
        withDrawRequest.setRole(WithDrawRole.PAYER.getValue());

        int currentPage = 1;
        if(StringUtils.isNotEmpty(request.getParameter("currentPage"))) {
            currentPage = Integer.parseInt(request.getParameter("currentPage"));
        }
        withDrawRequest.setCurrentPage(currentPage);

        if(StringUtils.isNotEmpty(request.getParameter("status"))) {
            withDrawRequest.setStatus(Integer.parseInt(request.getParameter("status")));
        }
        if(StringUtils.isNotEmpty(request.getParameter("mchId"))) {
            withDrawRequest.setMchId(request.getParameter("mchId"));
        }

        Pagenation<WithDrawInfo> pagenation = client.getWithDrawPage(withDrawRequest);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("pageInfo",pagenation);
        dispatchResult.addObject("requestInfo",withDrawRequest);
        dispatchResult.addObject("urlParams",String.format("status=%s&mchId=%s",withDrawRequest.getStatus()==null?"":withDrawRequest.getStatus(),withDrawRequest.getMchId()==null?"":withDrawRequest.getMchId()));
        return dispatchResult;
    }
}
