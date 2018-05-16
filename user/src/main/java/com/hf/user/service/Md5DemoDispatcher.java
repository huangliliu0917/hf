package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserGroup;
import com.hf.base.utils.Utils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class Md5DemoDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        UserGroup userGroup = client.getUserGroupById(groupId);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.addObject("cipherCode",userGroup.getCipherCode());
        dispatchResult.addObject("groupNo",userGroup.getGroupNo());
        dispatchResult.addObject("outTradeNo", RandomUtils.nextLong());
        dispatchResult.addObject("nonceStr", Utils.getRandomString(8));
        dispatchResult.setPage(page);
        return dispatchResult;
    }
}
