package com.hf.core.biz;

import com.hf.base.enums.ChannelProvider;
import com.hf.core.model.po.PayRequest;

import java.util.Map;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface PayBiz {
//    void checkParam(Map<String,Object> map);
//    Long savePayRequest(Map<String,Object> map);
    void doRemoteCall(PayRequest payRequest);
//    void promote(PayRequest payRequest);
//    void pay(PayRequest payRequest);
    void checkCallBack(Map<String, Object> map);
    void finishPay(Map<String, Object> map);
//    void paySuccess(String outTradeNo);
//    void payFailed(String outTradeNo);
//    RefundResponse refund(RefundRequest refundRequest);
//    ReverseResponse reverse(ReverseRequest reverseRequest);
    ChannelProvider getProvider();
}
