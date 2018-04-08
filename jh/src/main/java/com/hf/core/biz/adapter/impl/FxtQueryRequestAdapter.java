package com.hf.core.biz.adapter.impl;

import com.hf.base.contants.CodeManager;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.service.CacheService;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.model.dto.trade.query.FxtQueryRequest;
import com.hf.core.model.po.ChannelProvider;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserGroupExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class FxtQueryRequestAdapter implements Adapter<FxtQueryRequest> {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;

    @Override
    public FxtQueryRequest adpat(Map<String, Object> request) {
        String merchant_no = String.valueOf(request.get("mchId"));
        String out_trade_no = String.valueOf(request.get("outTradeNo"));

        UserGroup userGroup = cacheService.getGroup(merchant_no);

        if(Objects.isNull(userGroup)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"商户不存在");
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), com.hf.base.enums.ChannelProvider.FXT.getCode());

        FxtQueryRequest fxtQueryRequest = new FxtQueryRequest();
        fxtQueryRequest.setOut_trade_no(out_trade_no);
        fxtQueryRequest.setVersion("1.0");
        fxtQueryRequest.setMerchant_no(userGroupExt.getMerchantNo());
        fxtQueryRequest.setSign_type("MD5");
        fxtQueryRequest.setNonce_str(Utils.getRandomString(10));
        String sign = Utils.encrypt(MapUtils.beanToMap(fxtQueryRequest),userGroupExt.getCipherCode());
        fxtQueryRequest.setSign(sign);
        return fxtQueryRequest;
    }
}
