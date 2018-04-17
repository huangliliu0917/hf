package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.PayRequest;
import com.hfb.merchant.code.util.http.Httpz;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZfTradingBiz extends AbstractTradingBiz {

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.ZF;
    }
    @Autowired
    @Qualifier("zfClient")
    private PayClient payClient;

    @Override
    public void doPay(PayRequest payRequest, List<Header> headers) {
        try {
            String inTradeOrderNo = payRequest.getOutTradeNo();
            //todo merchantNo,terminalNo,attach什么区别
            String merchantNo = "990290077770049";
            String terminalNo = "77700624";
            String attach = "990290073720001";
            String key = "12345678";
            //todo 为什么订单金额和交易金额不一样
            String productPrice = String.valueOf(payRequest.getTotalFee());
            String payMoney = String.valueOf(payRequest.getTotalFee());
            //todo urlencode
            String productName = payRequest.getBody();
            String payType = "70";
            String merchantURL = payRequest.getOutNotifyUrl();
            String frontURL = payRequest.getOutNotifyUrl();
            String operatorId = payRequest.getBuyerId();
            //todo urlencode
            String productDescription = payRequest.getBody();
            //todo add field to payRequest
            String terminal = "PC";
            //todo 银行，名称或code?
            String defaultBank = payRequest.getBankCode();

            String signature = DigestUtils.md5Hex(merchantNo+terminalNo+ inTradeOrderNo +defaultBank+ payMoney + productPrice  + productName +payType+key).toUpperCase();
            String reqSource = "2";
            String url = "http://test1.guangyinwangluo.com:9999/swPayInterface/CHPay";
            Map<String,Object> map = new HashMap<>();
            map.put("inTradeOrderNo",inTradeOrderNo);
            map.put("merchantNo",merchantNo);
            map.put("terminalNo",terminalNo);
            map.put("productPrice",productPrice);
            map.put("payMoney",payMoney);
            map.put("productName",URLEncoder.encode(productName,"UTF-8"));
            map.put("payType",payType);
            map.put("merchantURL",merchantURL);
            map.put("frontURL",frontURL);
            map.put("operatorId",operatorId);
            map.put("productDescription",URLEncoder.encode(productDescription,"UTF-8"));
            map.put("terminal",terminal);
            map.put("defaultBank",URLEncoder.encode(defaultBank,"UTF-8"));
            map.put("signature",signature);
            map.put("reqSource",reqSource);
            map.put("attach",attach);
            Map<String,Object> resultMap = payClient.unifiedorder(map);
            System.out.println(new Gson().toJson(resultMap));
        } catch (Exception e) {
            throw new BizFailException(e);
        }
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        return null;
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
