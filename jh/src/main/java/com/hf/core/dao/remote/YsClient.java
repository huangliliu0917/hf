package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.model.RemoteParams;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Component
public class YsClient extends BaseClient implements PayClient {
    private static final String payUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=apiPay";
    private static final String queryUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=queryOrder";
    private static final String checkPayUrl = "https://www.uuplus.cc/index.php?g=Wap&m=BankPay&a=queryOrder";
    private Logger logger = LoggerFactory.getLogger(YsClient.class);

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        RemoteParams remoteParams = new RemoteParams(payUrl).withParams(params);
        String result = super.post(remoteParams);
        logger.info(String.format("unifiedorder finished,%s,%s",params.get("out_trade_no"),result));
        return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public Map<String, Object> refundorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> reverseorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> orderinfo(Map<String, Object> params) {
        RemoteParams remoteParams = new RemoteParams(queryUrl).withParams(params);
        String result = super.post(remoteParams);
        return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }

//    public String pay(Map<String,Object> data) {
//        RemoteParams params = new RemoteParams(payUrl).withParams(data);
//        return super.post(params);
//    }
//
//    public Map<String,String> getPayResult(String mchId,String outTradeNo) {
//        return MapUtils.buildMap("trade_state","SUCCESS","out_trade_no",outTradeNo);
////        RemoteParams params = new RemoteParams(checkPayUrl).withParam("mch_id",mchId).withParam("out_trade_no",outTradeNo);
////        String result = super.post(params);
////        return new Gson().fromJson(result,new TypeToken<Map<String,String>>(){}.getType());
//    }


    @Override
    public Map<String, Object> unifiedorder(List<Header> headers, Map<String, Object> params) {
        return null;
    }
}
