package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.GroupStatus;
import com.hf.base.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthorizedDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String userId = request.getSession().getAttribute("userId").toString();
        String groupId = request.getSession().getAttribute("groupId").toString();

        UserGroup userGroup = client.getUserGroupById(Long.parseLong(groupId));

        DispatchResult dispatchResult = new DispatchResult();

        switch (GroupStatus.parse(userGroup.getStatus())) {
            case NEW:
                dispatchResult.setPage("index_for_new_user");
                break;
            case SUBMITED:
                dispatchResult.setPage("user_account_authorized");
                break;
            case AVAILABLE:
                dispatchResult.setPage("index");
                break;
            case CANCEL:
                break;
        }

        return dispatchResult;
    }
}
