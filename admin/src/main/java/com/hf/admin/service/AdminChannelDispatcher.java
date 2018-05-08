package com.hf.admin.service;

import com.hf.admin.rpc.AdminClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.ChannelCode;
import com.hf.base.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
            String desc = channel.getChannelDesc();
            if( channel.getMinPrice().compareTo(BigDecimal.ZERO)>0) {
                desc = desc+","+"起投金额:"+channel.getMinPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)+"元";
            }
            if(channel.getMaxPrice().compareTo(BigDecimal.ZERO)>0) {
                desc = desc+","+"金额上限:"+channel.getMaxPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)+"元";
            }
            if(channel.getStartHour()>0 || channel.getStopHour()>0) {
                desc = desc+",交易时间:"+channel.getStartHour()+"-"+channel.getStopHour();
            }
            channel.setChannelDesc(desc);
        });
        dispatchResult.addObject("channels",channelList);
        return dispatchResult;
    }
}
