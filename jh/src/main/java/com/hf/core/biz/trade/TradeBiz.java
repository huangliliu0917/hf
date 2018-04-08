package com.hf.core.biz.trade;

import com.hf.core.model.po.PayRequest;

import java.util.Map;

public interface TradeBiz {
    Map<String,Object> pay(Map<String, Object> requestMap);
    Map<String,Object> query(PayRequest payRequest);
    Map<String,Object> refund(Map<String, Object> requestMap);
    void doPayFlow(PayRequest payRequest);
    void handleProcessingRequest(PayRequest payRequest);
    String handleCallBack(Map<String, String> map);
    void notice(PayRequest payRequest);
    void finishPay(Map<String, String> map);
}
