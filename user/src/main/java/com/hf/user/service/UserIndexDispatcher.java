package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.contants.Constants;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.GroupStatus;
import com.hf.base.model.UserGroup;
import com.hf.base.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.hf.base.contants.UserConstants.*;
import static com.hf.base.contants.UserConstants.ID;

@Service
public class UserIndexDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient hfClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        HttpSession session = request.getSession();
        Map<String,Object> sessionInfo = (Map<String,Object>)session.getAttribute(Constants.USER_LOGIN_INFO);
        Long userId = Long.parseLong(sessionInfo.get(ID).toString());
        UserInfo userInfo = hfClient.getUserInfoById(userId);

        Long groupId = userInfo.getGroupId();
        UserGroup userGroup = hfClient.getUserGroupById(groupId);

        DispatchResult result = new DispatchResult();
        result.addObject("name",sessionInfo.get("name"));

        switch (GroupStatus.parse(userGroup.getStatus())) {
            case NEW:
                result.setPage("index_for_new_user");
                result.addObject("userInfo",userInfo);
                break;
            case SUBMITED:
                result.setPage("user_account_authorized");
                result.addObject("userInfo",userInfo);
                break;
            case AVAILABLE:
                result.setPage("index");
                result.addObject("userInfo",userInfo);
                break;
            case CANCEL:
                break;
        }
        return result;
    }
}