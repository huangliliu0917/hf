package com.hf.admin.service;

import com.hf.base.client.AdminClient;
import com.hf.base.contants.Constants;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserGroup;
import com.hf.base.utils.Pagenation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AuthorizedDispatcher implements Dispatcher {
    @Autowired
    private AdminClient adminClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        int currentPage = 1;
        if(!StringUtils.isEmpty(request.getParameter("currentPage"))) {
            currentPage = Integer.parseInt(request.getParameter("currentPage"))<=0?currentPage:Integer.parseInt(request.getParameter("currentPage"));
        }

        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        List<UserGroup> list = adminClient.getAuthorizedList(groupId);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);

        Pagenation<UserGroup> pagenation = new Pagenation<UserGroup>(list, currentPage, Constants.pageSize);
        dispatchResult.addObject("pageInfo",pagenation);

        return dispatchResult;
    }
}
