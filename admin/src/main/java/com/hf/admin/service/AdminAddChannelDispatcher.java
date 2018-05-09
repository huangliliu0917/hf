package com.hf.admin.service;

import com.hf.admin.utils.MapUtils;
import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.model.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminAddChannelDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        String channelId = request.getParameter("channelId");
        if(!StringUtils.isEmpty(channelId)) {
            Channel channel = defaultClient.getChannelById(channelId);
            BigDecimal maxPrice = channel.getMaxPrice().divide(new BigDecimal("100"),4,BigDecimal.ROUND_HALF_UP);
            BigDecimal minPrice = channel.getMinPrice().divide(new BigDecimal("100"),4,BigDecimal.ROUND_HALF_UP);
            channel.setMaxPrice(maxPrice);
            channel.setMinPrice(minPrice);
            dispatchResult.addObject("channel",channel);
        }

        List<Map<String,String>> providers = new ArrayList<>();
        for(ChannelProvider channelProvider:ChannelProvider.values()) {
            providers.add(MapUtils.buildMap("code",channelProvider.getCode(),"name",channelProvider.getName()));
        }
        dispatchResult.addObject("providers",providers);

        List<Map<String,String>> channelNoMap = new ArrayList<>();
        for(ChannelCode channelCode:ChannelCode.values()) {
            channelNoMap.add(MapUtils.buildMap("code",channelCode.getCode(),"name",channelCode.getDesc()));
        }
        dispatchResult.addObject("channelNos",channelNoMap);

        return dispatchResult;
    }
}
