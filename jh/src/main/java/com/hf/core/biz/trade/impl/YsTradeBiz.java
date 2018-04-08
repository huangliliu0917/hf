package com.hf.core.biz.trade.impl;

import com.hf.base.utils.Utils;
import com.hf.core.biz.PayBiz;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.adapter.impl.YsPayRequestAdapter;
import com.hf.core.biz.adapter.impl.YsPayResponseAdapter;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class YsTradeBiz extends AbstractTradeBiz {
    @Autowired
    private YsPayRequestAdapter ysPayRequestAdapter;
    @Autowired
    private YsPayResponseAdapter ysPayResponseAdapter;
    @Autowired
    @Qualifier("ysClient")
    private PayClient payClient;
    @Autowired
    @Qualifier("ysPayBiz")
    private PayBiz payBiz;

    @Override
    Adapter getRequestAdapter() {
        return ysPayRequestAdapter;
    }

    @Override
    Adapter getResponseAdapter() {
        return ysPayResponseAdapter;
    }

    @Override
    PayClient getClient() {
        return payClient;
    }

    @Override
    String getSign(Map<String, Object> map, String cipherCode) {
        return Utils.encrypt2(map,cipherCode);
    }

    @Override
    PayBiz getPayBiz() {
        return payBiz;
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }

    @Override
    public Map<String, Object> refund(Map<String, Object> requestMap) {
        return null;
    }
}
