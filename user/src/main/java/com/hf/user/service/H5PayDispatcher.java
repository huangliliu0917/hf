package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserChannel;
import com.hf.base.utils.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class H5PayDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        List<UserChannel> channels = client.getUserChannelList(Long.parseLong(groupId));
        List<UserChannel> h5Channel = channels.parallelStream().filter(userChannel -> StringUtils.equalsIgnoreCase(userChannel.getChannelCode(),"04") || StringUtils.equalsIgnoreCase(userChannel.getChannelCode(),"10")).collect(Collectors.toList());

        DispatchResult dispatchResult = new DispatchResult();

        if(CollectionUtils.isEmpty(h5Channel)) {
            page = "channel_no_right";
            dispatchResult.addObject("pageName","H5支付");
            dispatchResult.addObject("channelName","H5支付");
            dispatchResult.setPage(page);
            return dispatchResult;
        }

        dispatchResult.setPage(page);

        List<Map<String,String>> payTypes = new ArrayList<>();
        h5Channel.forEach(userChannel -> {
            if(userChannel.getChannelCode().equals("04")) {
                payTypes.add(MapUtils.buildMap("code","1","name","微信H5支付"));
            }
            if(userChannel.getChannelCode().equals("10")) {
                payTypes.add(MapUtils.buildMap("code","3","name","QQ H5支付"));
            }
        });

        dispatchResult.addObject("payTypes",payTypes);

        return dispatchResult;
    }
}
