package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.biz.service.impl.PayServiceImpl;
import com.hfb.merchant.code.util.http.Httpz;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class WhpClient implements PayClient {

    private static final String PAY_URL = "http://pay.weihupay.cn/payinfo.php";
    private Logger logger = LoggerFactory.getLogger(WhpClient.class);

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        try {
            String msg = new Httpz("utf-8", 30000, 30000).post(PAY_URL, params);
            Map<String,Object> resultMap = new Gson().fromJson(msg,new TypeToken<Map<String,Object>>(){}.getType());
            logger.info(params.get("out_tradeid")+": pay result"+new Gson().toJson(resultMap));
            return resultMap;
        } catch (Exception e) {
            logger.error("pay failed,"+e.getMessage()+"---"+new Gson().toJson(params));
            return null;
        }
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
