package com.hf.admin.service;

import com.hf.admin.rpc.AdminClient;
import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.*;
import com.hf.base.utils.Pagenation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AdminOrderStatisticsDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;
    @Autowired
    private AdminClient adminClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        int currentPage = 1;
        String pageIndex = request.getParameter("currentPage");
        if(!StringUtils.isEmpty(pageIndex)) {
            currentPage = Integer.parseInt(pageIndex);
        }
        TradeStatisticsRequest tradeRequest = new TradeStatisticsRequest();
        tradeRequest.setGroupId(groupId);
        tradeRequest.setCurrentPage(currentPage);
        tradeRequest.setPageSize(15);

        String groupNo ="";
        String createTime ="";

        Pagenation<TradeStatisticsRequestDto> pagenation = client.getTradeOrderStatisticsList(tradeRequest);

        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("pageInfo",pagenation);
        dispatchResult.addObject("requestInfo",tradeRequest);

        List<Channel> channels = adminClient.getChannelList();
        dispatchResult.addObject("channels",channels);
        dispatchResult.addObject("urlParams",
                String.format("groupNo=%s&createTime=%s",
                        StringUtils.isEmpty(groupNo)?"":groupNo,
                        StringUtils.isEmpty(createTime)?"":createTime)
                );
        return dispatchResult;
    }
}
