package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.model.RemoteParams;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ZfbClient extends BaseClient implements PayClient {

    protected Logger logger = LoggerFactory.getLogger(ZfbClient.class);

    private static final String PAY_URL = "https://api.likerpay.com/Pay_Index.html";

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        RemoteParams remoteParams = new RemoteParams(PAY_URL).withParams(params);
        HttpHeaders httpHeaders = new HttpHeaders();
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED,httpHeaders);
        logger.info("zfb2 tradeNo:"+params.get("pay_orderid")+", pay result:"+result);
        return  new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public Map<String, Object> unifiedorder(List<Header> headers, Map<String, Object> params) {
        return null;
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
        return null;
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }
}
