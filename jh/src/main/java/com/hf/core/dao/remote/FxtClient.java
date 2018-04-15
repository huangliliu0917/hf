package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.model.RemoteParams;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/28.
 */
@Component
public class FxtClient extends BaseClient implements PayClient {

    private Logger logger = LoggerFactory.getLogger(FxtClient.class);

    private String unifiedorderUrl = "http://pay.51fuxintong.com/wx.php/OpenApi/unifiedorder";
    private static final String orderInfoUrl = "http://pay.51fuxintong.com/wx.php/OpenApi/orderinfo";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        RemoteParams remoteParams = new RemoteParams(unifiedorderUrl).withParams(params);
        logger.info(new Gson().toJson(params));
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED);
        logger.info(String.format("unifiedorder finished,%s,%s",params.get("out_trade_no"),result));
        return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public Map<String, Object> refundorder(Map<String, Object> params) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> reverseorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> orderinfo(Map<String, Object> params) {
        RemoteParams remoteParams = new RemoteParams(orderInfoUrl).withParams(params);
        logger.info(new Gson().toJson(params));
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED);
        logger.info(String.format("orderinfo finished,%s,%s",params.get("out_trade_no"),result));
        return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }

//    public PayResponse unifiedorder(PayRequestDto payRequestDto) {
//        ResponseEntity<PayResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER, payRequestDto, PayResponse.class, Collections.EMPTY_MAP);
//        return response.getBody();
//    }
//
//    public RefundResponse refund(RefundRequest refundRequest) {
//        ResponseEntity<RefundResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER,refundRequest, RefundResponse.class, Collections.EMPTY_MAP);
//        return response.getBody();
//    }
//
//    public ReverseResponse reverse(ReverseRequest reverseRequest) {
//        ResponseEntity<ReverseResponse> response = restTemplate.postForEntity(URL+UNIFIEDORDER,reverseRequest, ReverseResponse.class, Collections.EMPTY_MAP);
//        return response.getBody();
//    }


    @Override
    public Map<String, Object> unifiedorder(List<Header> headers, Map<String, Object> params) {
        return null;
    }
}
