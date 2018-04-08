package com.hf.core.biz.adapter.impl;

import com.hf.base.enums.ChannelProvider;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.model.dto.trade.refund.HfRefundResponse;
import com.hf.core.model.enums.ErrCode;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserGroupExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FxtRefundResponseAdapter implements Adapter<HfRefundResponse> {
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PayService payService;

    @Override
    public HfRefundResponse adpat(Map<String, Object> request) {
        HfRefundResponse hfRefundResponse = new HfRefundResponse();

        String errcode = String.valueOf(request.get("errcode"));
        String message = String.valueOf(request.get("message"));

        hfRefundResponse.setErrcode(errcode);
        hfRefundResponse.setMessage(message);

        if(StringUtils.equals(errcode, String.valueOf(ErrCode.SUCCESS.getValue()))) {
            String out_trade_no = String.valueOf(request.get("out_trade_no"));
            PayRequest payRequest = payRequestDao.selectByTradeNo(out_trade_no);
            UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
            UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.FXT.getCode());
            if(!Utils.checkEncrypt(request,userGroupExt.getCipherCode())) {
                //处理中
                hfRefundResponse.setErrcode("4");
                return hfRefundResponse;
            }
            payService.finishRefund(payRequest);

            hfRefundResponse.setNo(String.valueOf(payRequest.getId()));
            hfRefundResponse.setOut_trade_no(out_trade_no.split(",")[1]);
            hfRefundResponse.setRefund_fee(String.valueOf(payRequest.getTotalFee()));
            hfRefundResponse.setSign_type("MD5");

            String sign = Utils.encrypt(MapUtils.beanToMap(hfRefundResponse),userGroup.getCipherCode());
            hfRefundResponse.setSign(sign);
            return hfRefundResponse;
        }

        return hfRefundResponse;
    }
}
