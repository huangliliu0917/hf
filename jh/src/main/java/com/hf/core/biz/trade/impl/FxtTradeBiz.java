package com.hf.core.biz.trade.impl;

import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.PayBiz;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.adapter.impl.*;
import com.hf.core.biz.service.CacheService;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.dto.trade.query.FxtQueryRequest;
import com.hf.core.model.dto.trade.refund.FxtRefundRequest;
import com.hf.core.model.dto.trade.refund.HfRefundResponse;
import com.hf.core.model.po.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FxtTradeBiz extends AbstractTradeBiz {
    @Autowired
    @Qualifier("fxtClient")
    private PayClient payClient;
    @Autowired
    @Qualifier("fxtPayBiz")
    private PayBiz payBiz;
    @Autowired
    private FxtPayRequestAdapter fxtPayRequestAdapter;
    @Autowired
    private FxtPayResponseAdapter fxtPayResponseAdapter;
    @Autowired
    private FxtQueryRequestAdapter fxtQueryRequestAdapter;
    @Autowired
    private FxtRefundRequestAdapter fxtRefundRequestAdapter;
    @Autowired
    private FxtRefundResponseAdapter fxtRefundResponseAdapter;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;

    @Override
    Adapter getRequestAdapter() {
        return fxtPayRequestAdapter;
    }

    @Override
    Adapter getResponseAdapter() {
        return fxtPayResponseAdapter;
    }

    @Override
    PayClient getClient() {
        return payClient;
    }

    @Override
    String getSign(Map<String, Object> map, String cipherCode) {
        return Utils.encrypt(map,cipherCode);
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        FxtQueryRequest fxtQueryRequest = fxtQueryRequestAdapter.adpat(MapUtils.beanToMap(payRequest));
        Map<String,Object> resultMap = payClient.orderinfo(MapUtils.beanToMap(fxtQueryRequest));

//        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
//        UserGroupExt userGroupExt =userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.FXT.getCode());

//        if(!Utils.checkEncrypt(resultMap,userGroupExt.getCipherCode())) {
//            logger.warn(String.format("check encrypt failed,%s,%s,%s",));
//        }

        return resultMap;
    }

    @Override
    public Map<String, Object> refund(Map<String, Object> requestMap) {
        FxtRefundRequest fxtRefundRequest = fxtRefundRequestAdapter.adpat(requestMap);
        Map<String,Object> refundResult = payClient.refundorder(MapUtils.beanToMap(fxtRefundRequest));
        HfRefundResponse hfRefundResponse = fxtRefundResponseAdapter.adpat(refundResult);
        return MapUtils.beanToMap(hfRefundResponse);
    }

    @Override
    PayBiz getPayBiz() {
        return payBiz;
    }
}
