package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.model.RemoteParams;
import com.hf.base.utils.MapUtils;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SytClient extends BaseClient implements PayClient {

    private static final String PAY_URL = "http://qr.sytpay.cn/api/v1/create.php";
    private static final Logger logger = LoggerFactory.getLogger(SytClient.class);

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        RemoteParams remoteParams = new RemoteParams(PAY_URL).withParams(params);
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED);
        logger.info("syt pay result : "+result);
        return MapUtils.buildMap("data",result);
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
