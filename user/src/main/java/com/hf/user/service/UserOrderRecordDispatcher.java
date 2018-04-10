package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.model.TradeRequest;
import com.hf.base.model.TradeRequestDto;
import com.hf.base.model.UserChannel;
import com.hf.base.utils.Pagenation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserOrderRecordDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);

        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        List<UserChannel> channels = client.getUserChannelList(groupId);
        dispatchResult.addObject("channels",channels);

        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setGroupId(groupId);
        int currentPage = 1;
        if(StringUtils.isNotEmpty(request.getParameter("currentPage"))) {
            currentPage = Integer.parseInt(request.getParameter("currentPage"));
        }

        tradeRequest.setCurrentPage(currentPage);
        tradeRequest.setPageSize(15);
        if(StringUtils.isNotEmpty(request.getParameter("channelCode"))) {
            tradeRequest.setChannelCode(request.getParameter("channelCode"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("status"))) {
            tradeRequest.setStatus(Integer.parseInt(request.getParameter("status")));
        } else {
            tradeRequest.setStatus(PayRequestStatus.PAY_SUCCESS.getValue());
        }
        if(StringUtils.isNotEmpty(request.getParameter("outTradeNo"))) {
            tradeRequest.setOutTradeNo(request.getParameter("outTradeNo"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("type"))) {
            tradeRequest.setType(Integer.parseInt(request.getParameter("type")));
        }

        Pagenation<TradeRequestDto> pagenation =  client.getTradeList(tradeRequest);
        dispatchResult.addObject("pageInfo",pagenation);
        dispatchResult.addObject("requestInfo",tradeRequest);

        return dispatchResult;
    }
}
