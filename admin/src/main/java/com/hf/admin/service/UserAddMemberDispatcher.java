package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserAddMemberDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client ;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getParameter("group");
        String userId = request.getParameter("userId");

        UserInfo userInfo = new UserInfo();
        if(!StringUtils.isEmpty(userId)) {
            userInfo = client.getUserInfoById(Long.parseLong(userId));
        }

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("group",groupId);
        dispatchResult.addObject("userInfo",userInfo);

        return dispatchResult;
    }
}
