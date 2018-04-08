package com.hf.core.biz.impl;

import com.hf.base.enums.ChannelProvider;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WwPayBiz extends AbstractPayBiz {
    @Override
    PayClient getPayClient() {
        return null;
    }

    @Override
    void handlePayResult(PayRequest payRequest, PayMsgRecord payMsgRecord, Map<String, Object> payResult) {

    }

    @Override
    public void checkCallBack(Map<String, Object> map) {

    }

    @Override
    public void finishPay(Map<String, Object> map) {

    }

    @Override
    public ChannelProvider getProvider() {
        return null;
    }
}
