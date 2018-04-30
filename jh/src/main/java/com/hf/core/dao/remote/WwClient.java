package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.enums.ChannelCode;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.model.RemoteParams;
import com.hf.base.utils.EpaySignUtil;
import com.hf.base.utils.HttpClient;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.CacheService;
import com.hf.core.utils.CipherUtils;
import com.hf.core.utils.Https;
import com.hfb.merchant.code.util.http.Httpz;
import org.apache.commons.httpclient.Header;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WwClient extends BaseClient implements PayClient {
    @Autowired
    private CacheService cacheService;
    protected Logger logger = LoggerFactory.getLogger(WwClient.class);

    private static final String H5_PAY_URL = "http://47.97.175.195:8692/pay/payment/toH5";
    private static final String WY_PAY_URL = "http://pay1.hlqlb.cn:8692/pay/payment/toPayment";
    private static final String QUERY_URL = "http://47.97.175.195:8682/posp/cashierDesk/orderQuery";
    private static final String QR_PAY_URL = "http://47.97.175.195:8682/posp/cashierDesk/qrcodePay";
    private static final String AGENT_PAY_APPLY = "http://47.97.175.195:8682/posp/agentPay/toApply";
    private static final String QUERY_AGENT_AMOUNT = "http://47.97.175.195:8682/posp/agentPay/balance";

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        return this.unifiedorder(new ArrayList<>(),params);
    }

    public String unifiedorder(Map<String,Object> params, HttpServletResponse response) throws Exception {
        String url = null;
        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"04") || StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"10") || StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"14")) {
            url = H5_PAY_URL;
        }
        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"09")) {
            url = WY_PAY_URL;
        }
        if(url != null) {
            params.remove("channelCode");
            String sign = EpaySignUtil.sign(CipherUtils.private_key, Utils.getEncryptStr(params));
            params.put("signStr",sign);

            HttpClient httpClient = new HttpClient(response);
            httpClient.setParameter(params);
            httpClient.sendByPost(url);
            return "";
        }
        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(), ChannelCode.WX_SM.getCode()) || StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),ChannelCode.ALI_ZS.getCode())) {
            url = QR_PAY_URL;
        }
        if(url == null) {
            throw new BizFailException("channelCode not exist");
        }
        params.remove("channelCode");
        String sign = EpaySignUtil.sign(CipherUtils.private_key, Utils.getEncryptStr(params));
        params.put("signStr",sign);
        return (new com.hfb.merchant.quick.util.http.Httpz("utf-8", 30000, 30000)).post(url, params);
    }

    @Override
    public Map<String, Object> unifiedorder(List<Header> headers, Map<String, Object> params) {
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
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(header -> {
            logger.info("remote call request header:"+header.getName()+",value:"+header.getValue());
            httpHeaders.add(header.getName(),header.getValue());
        });
        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED,httpHeaders);
//        Https https = new Https();
//        List<NameValuePair> nameValuePairs = new ArrayList<>();
//        params.keySet().forEach(s -> {
//            NameValuePair nameValuePair = new NameValuePair(s,String.valueOf(params.get(s)));
//            nameValuePairs.add(nameValuePair);
//        });
//
//        NameValuePair[] nameValuePairsArray = new NameValuePair[nameValuePairs.size()];
//
//        String result = https.sendAsPost(url,nameValuePairs.toArray(nameValuePairsArray),headers);

        String outTradeNo = String.valueOf(params.get("orderNum"));
        return MapUtils.buildMap("payResult",result,"outTradeNo",outTradeNo);
    }

//    @Override
//    public Map<String, Object> unifiedorder(HttpHeaders headers, Map<String, Object> params) {
//        String url = null;
//        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"04") || StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"10")) {
//            url = H5_PAY_URL;
//        }
//        if(StringUtils.equalsIgnoreCase(params.get("channelCode").toString(),"09")) {
//            url = WY_PAY_URL;
//        }
//        if(url == null) {
//            throw new BizFailException("channelCode not exist");
//        }
//        params.remove("channelCode");
//        String sign = EpaySignUtil.sign(CipherUtils.private_key, Utils.getEncryptStr(params));
//        params.put("signStr",sign);
//
//        RemoteParams remoteParams = new RemoteParams(url).withParams(params);
//        headers = new HttpHeaders();
//        String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED,headers);
//        String outTradeNo = String.valueOf(params.get("orderNum"));
//        return MapUtils.buildMap("payResult",result,"outTradeNo",outTradeNo);
//    }


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

    public Map<String,Object> agentPayApply(Map<String,Object> params) {
        try {
            String msg = new Httpz("utf-8", 30000, 30000).post(AGENT_PAY_APPLY, params);
            logger.info("agent pay apply result:"+msg);
            return new Gson().fromJson(msg,new TypeToken<Map<String,Object>>(){}.getType());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<String,Object> getAgentAmount(Map<String,Object> params) {
        try {
            String msg = new Httpz("utf-8", 30000, 30000).post(QUERY_AGENT_AMOUNT, params);
            logger.info("agent pay apply result:"+msg);
            return new Gson().fromJson(msg,new TypeToken<Map<String,Object>>(){}.getType());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new HashMap<>();
        }
    }
}
