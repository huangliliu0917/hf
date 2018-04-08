package com.hf.agent.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserChannel;
import com.hf.base.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AgentChannelDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        List<UserChannel> channels = client.getUserChannelList(groupId);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("channels",channels);

        dispatchResult.addObject("payUrl","http://huifufu.cn/hf-pay/pay");
        dispatchResult.addObject("refundUrl","http://huifufu.cn/hf-pay/refund");
        dispatchResult.addObject("queryUrl","http://huifufu.cn/hf-pay/queryOrder");

        UserGroup userGroup = client.getUserGroupById(groupId);
        dispatchResult.addObject("groupNo",userGroup.getGroupNo());
        dispatchResult.addObject("callBackUrl",userGroup.getCallbackUrl());
        dispatchResult.addObject("cipherCode",userGroup.getCipherCode());
        return dispatchResult;
    }
}
