package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.model.RemoteParams;
import com.hf.base.utils.EpaySignUtil;
import com.hf.base.utils.HttpClient;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.CacheService;
import com.hf.core.utils.CipherUtils;
import com.hfb.merchant.code.util.http.Httpz;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Map;

@Component
public class WwClient extends BaseClient implements PayClient {
    @Autowired
    private CacheService cacheService;

    private static final String H5_PAY_URL = "http://47.97.175.195:8692/pay/payment/toH5";
    private static final String WY_PAY_URL = "http://pay1.hlqlb.cn:8692/pay/payment/toPayment";
    private static final String QUERY_URL = "http://47.97.175.195:8682/posp/cashierDesk/orderQuery";

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> unifiedorder(HttpHeaders headers, Map<String, Object> params) {
        String url = null;
        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"04") || StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"10")) {
            url = H5_PAY_URL;
        }
        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"09")) {
            url = WY_PAY_URL;
        }
        if(url == null) {
            throw new BizFailException("channelCode not exist");
        }
        params.remove("channelCode");
        String sign = EpaySignUtil.sign(CipherUtils.private_key, Utils.getEncryptStr(params));
        params.put("signStr",sign);

        RemoteParams remoteParams = new RemoteParams(url).withParams(params);
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED,headers);
        String outTradeNo = String.valueOf(params.get("orderNum"));
        return MapUtils.buildMap("payResult",result,"outTradeNo",outTradeNo);
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
        RemoteParams remoteParams = new RemoteParams(QUERY_URL).withParams(params);
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED);
        return new Gson().fromJson(result,new TypeToken<Map<String,String>>(){}.getType());
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }
}
