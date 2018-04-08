package com.hf.core.biz.adapter.impl;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.OperateType;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.enums.TradeType;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.TypeConverter;
import com.hf.base.utils.Utils;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.PayMsgRecordDao;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.model.PropertyConfig;
import com.hf.core.model.dto.trade.refund.FxtRefundRequest;
import com.hf.core.model.dto.trade.refund.HfRefundRequest;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserGroupExt;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class FxtRefundRequestAdapter implements Adapter<FxtRefundRequest> {
    private Logger logger = LoggerFactory.getLogger(FxtRefundRequestAdapter.class);
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PropertyConfig propertyConfig;
    @Autowired
    private PayService payService;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;

    @Override
    public FxtRefundRequest adpat(Map<String, Object> request) {
        HfRefundRequest hfRefundRequest = TypeConverter.convert(request, HfRefundRequest.class);
        UserGroup userGroup = cacheService.getGroup(hfRefundRequest.getMerchant_no());
        if(Objects.isNull(userGroup)) {
            logger.warn(String.format("商户不存在,%s",hfRefundRequest.getMerchant_no()));
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,String.format("商户不存在,%s",hfRefundRequest.getMerchant_no()));
        }
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.FXT.getCode());
        if(!Utils.checkEncrypt(request,userGroupExt.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        PayRequest payRequest = null;
        if(!StringUtils.isEmpty(hfRefundRequest.getOri_order_no())) {
            String tradeNo = String.format("%s_%s",hfRefundRequest.getMerchant_no(),hfRefundRequest.getOri_order_no());
            payRequest = payRequestDao.selectByTradeNo(tradeNo);
        }
        if(!StringUtils.isEmpty(hfRefundRequest.getOri_no())) {
            payRequest = payRequestDao.selectByPrimaryKey(Long.parseLong(hfRefundRequest.getOri_no()));
        }

        if(Objects.isNull(payRequest)) {
            logger.warn(String.format("PayRequest not exist,merchant:%s,out_no:%s,no:%s",hfRefundRequest.getMerchant_no(),hfRefundRequest.getOri_order_no(),hfRefundRequest.getOri_no()));
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易不存在");
        }

        String tradeNo = String.format("%s_%s",hfRefundRequest.getMerchant_no(),hfRefundRequest.getOri_order_no());
        String refundNo = String.format("%s_%s",hfRefundRequest.getMerchant_no(),hfRefundRequest.getRefund_no());

        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus());

        switch (payRequestStatus) {
            case NEW:
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易未完成，不能退款");
            case OPR_GENERATED:
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易未完成，不能退款");
            case PROCESSING:
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易未完成，不能退款");
            case PAY_FAILED:
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易已失败，不能退款");
            case OPR_SUCCESS:
                if(!StringUtils.equals(payRequest.getPayResult(),"0")) {
                    throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易已失败，不能退款");
                }
                break;
            case REFUNDING:
                PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(tradeNo,OperateType.USER_HF.getValue(),TradeType.REFUND.getValue());
                if(Objects.isNull(payMsgRecord)) {
                    throw new BizFailException(String.format("no msg record exist,tradeNo:%s",tradeNo));
                }
                FxtRefundRequest fxtRefundRequest = new Gson().fromJson(payMsgRecord.getMsgBody(),FxtRefundRequest.class);
                String sign = Utils.encrypt(MapUtils.beanToMap(fxtRefundRequest),payMsgRecord.getCipherCode());
                fxtRefundRequest.setSign(sign);
                return fxtRefundRequest;
            case OPR_FINISHED:
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易已失败，不能退款");
            case PAY_SUCCESS:
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"交易已完成，不能退款");
        }

        FxtRefundRequest fxtRefundRequest = new FxtRefundRequest();
        fxtRefundRequest.setMerchant_no(userGroupExt.getMerchantNo());
        fxtRefundRequest.setOri_order_no(tradeNo);
        fxtRefundRequest.setOut_notify_url(propertyConfig.getCallbackUrl());
        fxtRefundRequest.setRefund_fee(String.valueOf(payRequest.getTotalFee()));
        fxtRefundRequest.setRefund_no(refundNo);
        fxtRefundRequest.setSign_type("MD5");
        fxtRefundRequest.setVersion("1.0");
        fxtRefundRequest.setNonce_str(Utils.getRandomString(8));
        String sign = Utils.encrypt(MapUtils.beanToMap(fxtRefundRequest),userGroupExt.getCipherCode());
        fxtRefundRequest.setSign(sign);

        PayMsgRecord payMsgRecord = new PayMsgRecord(tradeNo,hfRefundRequest.getMerchant_no(),payRequest.getService(), OperateType.CLIENT_HF.getValue(), TradeType.REFUND.getValue(), userGroupExt.getCipherCode(), MapUtils.beanToMap(fxtRefundRequest));
        payService.startRefund(payRequest,payMsgRecord);
        return fxtRefundRequest;
    }
}
