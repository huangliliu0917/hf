package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.contants.Constants;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserInfo;
import com.hf.base.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.hf.base.contants.UserConstants.ID;

@Service
public class UserProfileDispatcher implements Dispatcher {

    @Autowired
    private DefaultClient hfClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        HttpSession session = request.getSession();
        Map<String,Object> sessionInfo = (Map<String,Object>)session.getAttribute(Constants.USER_LOGIN_INFO);
        Long userId = Long.parseLong(sessionInfo.get(ID).toString());
        UserInfo userInfo = hfClient.getUserInfoById(userId);

        DispatchResult result = new DispatchResult();
        result.setPage(page);
        result.setData(MapUtils.buildMap("userInfo",userInfo));

        return result;
    }
}
