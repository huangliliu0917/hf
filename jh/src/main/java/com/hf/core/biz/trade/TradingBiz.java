package com.hf.core.biz.trade;

import java.util.Map;

public interface TradingBiz {
    Map<String,Object> pay(Map<String,Object> requestMap);
}
