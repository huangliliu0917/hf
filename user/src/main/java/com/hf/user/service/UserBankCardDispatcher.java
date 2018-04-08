package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserBankCard;
import com.hf.base.model.UserGroup;
import com.hf.base.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserBankCardDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        List<UserBankCard> list = client.getUserBankCard(groupId);
        DispatchResult result = new DispatchResult();
        result.setPage(page);
        result.setData(MapUtils.buildMap("cards",list));
        UserGroup userGroup = client.getUserGroupById(groupId);
        result.addObject("groupInfo",userGroup);
        return result;
    }
}
