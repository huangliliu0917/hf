package com.hf.core.biz;

import com.hf.base.model.TradeRequest;
import com.hf.base.model.TradeRequestDto;
import com.hf.base.utils.Pagenation;

import java.util.Map;

public interface TrdBiz {

    Pagenation<TradeRequestDto> getTradeList(TradeRequest request);

    Map<String,Object> orderInfo(String outTradeNo);
}
