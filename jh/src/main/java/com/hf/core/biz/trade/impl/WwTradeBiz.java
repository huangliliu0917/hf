package com.hf.core.biz.trade.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.OperateType;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.enums.TradeType;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.EpaySignUtil;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.PayBiz;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.adapter.impl.WwPayRequestAdapter;
import com.hf.core.biz.adapter.impl.WwPayResponseAdapter;
import com.hf.core.biz.service.CacheService;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserGroupExt;
import com.hf.core.utils.CipherUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WwTradeBiz extends AbstractTradeBiz {
    @Autowired
    private WwPayRequestAdapter wwPayRequestAdapter;
    @Autowired
    @Qualifier("wwClient")
    private PayClient wwClient;
    @Autowired
    private WwPayResponseAdapter wwPayResponseAdapter;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;

    private static final String[] callbackFields = {"memberCode","orderNum","payNum","payType","payMoney","payTime","platformType","interfaceType","respType","resultCode","resultMsg","signStr"};

    @Override
    Adapter getRequestAdapter() {
        return wwPayRequestAdapter;
    }

    @Override
    Adapter getResponseAdapter() {
        return wwPayResponseAdapter;
    }

    @Override
    PayClient getClient() {
        return wwClient;
    }

    @Override
    String getSign(Map<String, Object> map, String cipherCode) {
        return null;
    }

    @Override
    PayBiz getPayBiz() {
        return null;
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.WW.getCode());
        Map<String,Object> params = new HashMap<>();
        params.put("memberCode",userGroupExt.getMerchantNo());
        params.put("orderNum",payRequest.getOutTradeNo());
        String signUrl = Utils.getEncryptStr(params);
        String signStr = EpaySignUtil.sign(CipherUtils.private_key,signUrl);
        params.put("signStr",signStr);
        Map<String,Object> resultMap = wwClient.orderinfo(params);
        if(org.apache.commons.collections.MapUtils.isEmpty(resultMap)) {
            resultMap.put("errcode",99);
            throw new BizFailException("result is null");
        }
        resultMap.put("errcode",0);
        String orderCode = String.valueOf(resultMap.get("orderCode"));
        String oriRespCode = String.valueOf(resultMap.get("oriRespCode"));
        String returnCode = String.valueOf(resultMap.get("returnCode"));
        String oriRespMsg = String.valueOf(resultMap.get("oriRespMsg"));

        resultMap.put("out_trade_no",orderCode);
        resultMap.put("message",oriRespMsg);

        logger.warn(String.format("%s query ww status : %s,%s",payRequest.getOutTradeNo(),oriRespCode,returnCode));

        if(!StringUtils.equals(returnCode,"0000")) {
            logger.warn(String.format("%s query failed,%s",payRequest.getOutTradeNo(),new Gson().toJson(resultMap)));
            throw new BizFailException("retry");
        }

        if(StringUtils.equals(oriRespCode,"555555")) {
            throw new BizFailException("retry");
        }

        if(StringUtils.equals(oriRespCode,"000000")) {
            resultMap.put("status",1);
        } else {
            resultMap.put("status",5);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> refund(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public void doRemotePay(PayRequest payRequest) {
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(payRequest.getOutTradeNo(), OperateType.HF_CLIENT.getValue(), TradeType.PAY.getValue());
        PayMsgRecord hfResultMsg = payMsgRecordDao.selectByTradeNo(payMsgRecord.getOutTradeNo(),OperateType.HF_USER.getValue(),TradeType.PAY.getValue());
        if(!Objects.isNull(hfResultMsg)) {
            return;
        }
        Map<String,Object> map = new Gson().fromJson(payMsgRecord.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        Map<String,Object> resultMap = getClient().unifiedorder(map);

        if(null != resultMap) {
            resultMap.put("inputMsg",payMsgRecord);
        }
        getResponseAdapter().adpat(resultMap);
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        for(String field:callbackFields) {
            if(Objects.isNull(map.get(field)) || Utils.isEmpty(String.valueOf(map.get(field)))) {
                throw new BizFailException(String.format("%s不能为空",field));
            }
        }
        String memberCode = map.get("memberCode");
        String orderNum = map.get("orderNum");
        String payNum = map.get("payNum");
        int payType = new BigDecimal(map.get("payType")).intValue();
        BigDecimal payMoney = new BigDecimal(map.get("payMoney"));
        String payTime = map.get("payTime");
        int platformType = new BigDecimal(map.get("platformType")).intValue();
        int interfaceType = new BigDecimal(map.get("interfaceType")).intValue();
        int respType = new BigDecimal(map.get("respType")).intValue();
        String resultMsg = map.get("resultMsg");
        String signStr = map.get("signStr");

        PayRequest payRequest = payRequestDao.selectByTradeNo(orderNum);
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;

        if(payRequestStatus == PayRequestStatus.OPR_SUCCESS) {
            return new Gson().toJson(MapUtils.buildMap("resCode","0000"));
        }

        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        if(respType == 1) {
            return new Gson().toJson(MapUtils.buildMap("resCode","0000"));
        }

        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());

        PayMsgRecord payMsgRecord = new PayMsgRecord(orderNum,userGroup.getGroupNo(),payRequest.getService(),OperateType.CALLBACK_CLIENT_HF.getValue(),TradeType.PAY.getValue(),map);
        payService.savePayMsg(payMsgRecord);

        if(respType == 2) {
            payService.paySuccess(orderNum);
        }

        if(respType == 3) {
            payService.payFailed(orderNum);
        }

        return new Gson().toJson(MapUtils.buildMap("resCode","0000"));
    }

    @Override
    public void finishPay(Map<String, String> map) {
        String tradeNo = map.get("orderNum");
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        if(Objects.isNull(payRequest)) {
            throw new BizFailException("payRequest is null");
        }

        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;
        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_SUCCESS) {
            throw new BizFailException("status invalid");
        }

        String mch_id = payRequest.getMchId();

        int respType = new BigDecimal(map.get("respType")).intValue();
        if(respType == 1) {
            return;
        }

        PayMsgRecord payMsgRecord = new PayMsgRecord(tradeNo,mch_id,payRequest.getService(),OperateType.CALLBACK_CLIENT_HF.getValue(),TradeType.PAY.getValue(),map);
        payService.savePayMsg(payMsgRecord);

        if(respType == 2) {
            payService.paySuccess(tradeNo);
        } else {
            payService.payFailed(tradeNo);
        }
    }
}
