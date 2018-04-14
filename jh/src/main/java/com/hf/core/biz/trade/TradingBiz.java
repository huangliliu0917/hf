package com.hf.core.biz.trade;

import com.hf.core.model.po.PayRequest;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface TradingBiz {
    Map<String,Object> pay(Map<String,Object> requestMap, HttpHeaders headers);
    String handleCallBack(Map<String,String> map);
    void notice(PayRequest payRequest);
    void handleProcessingRequest(PayRequest payRequest);
    Map<String,Object> query(PayRequest payRequest);
}
