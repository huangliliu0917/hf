package com.hf.core.biz.trade;

import com.hf.core.model.po.PayRequest;

import java.util.Map;

public interface TradingBiz {
    Map<String,Object> pay(Map<String,Object> requestMap);
    String handleCallBack(Map<String,String> map);
    void notice(PayRequest payRequest);
}
