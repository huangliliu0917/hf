package com.hf.core.biz.trade;

import com.hf.core.model.po.PayRequest;
import org.apache.commons.httpclient.Header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface TradingBiz {
    Map<String,Object> pay(Map<String,Object> requestMap, HttpServletRequest request, HttpServletResponse response);
    Map<String,Object> pay(Map<String,Object> requestMap);
    String handleCallBack(Map<String,String> map);
    void handleCallBack(PayRequest payRequest);
    void notice(PayRequest payRequest);
    void handleProcessingRequest(PayRequest payRequest);
    Map<String,Object> query(PayRequest payRequest);
}
