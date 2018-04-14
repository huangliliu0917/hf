package com.hf.core.biz;

import com.hf.base.model.*;
import com.hf.base.utils.Pagenation;

import java.util.List;
import java.util.Map;

public interface TrdBiz {

    Pagenation<TradeRequestDto> getTradeList(TradeRequest request);

    Pagenation<TradeStatisticsRequestDto> getTradeStatisticsList(TradeStatisticsRequest  request);

    Map<String,Object> orderInfo(String outTradeNo);

    List<UserStatistic> getUserStatistics(TradeStatisticsRequest  request);
}
