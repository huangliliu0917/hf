package com.hf.admin.service;

import com.hf.admin.rpc.AdminClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.ChannelCode;
import com.hf.base.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AdminChannelDispatcher implements Dispatcher {
    @Autowired
    private AdminClient adminClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);

        List<Channel> channelList = adminClient.getChannelList();
        channelList.parallelStream().forEach(channel -> {
            ChannelCode channelCode = ChannelCode.parseFromCode(channel.getCode());
            channel.setCode(channelCode.getDesc());
        });
        dispatchResult.addObject("channels",channelList);
        return dispatchResult;
    }
}
