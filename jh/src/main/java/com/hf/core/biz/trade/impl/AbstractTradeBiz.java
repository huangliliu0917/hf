package com.hf.core.biz.trade.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.enums.OperateType;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.enums.TradeType;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.Utils;
import com.hf.core.biz.PayBiz;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.service.PayService;
import com.hf.core.biz.trade.TradeBiz;
import com.hf.core.dao.local.PayMsgRecordDao;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.dao.remote.CallBackClient;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.PropertyConfig;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTradeBiz implements TradeBiz {
    protected Logger logger = LoggerFactory.getLogger(AbstractTradeBiz.class);

    @Autowired
    protected PayRequestDao payRequestDao;
    @Autowired
    protected PayService payService;
    @Autowired
    protected PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private CallBackClient callBackClient;
    @Autowired
    private PropertyConfig propertyConfig;

    protected static final Map<String,Long> noticeRequestMap = new ConcurrentHashMap<>();

    abstract Adapter getRequestAdapter();

    abstract Adapter getResponseAdapter();

    abstract PayClient getClient();

    abstract String getSign(Map<String,Object> map,String cipherCode);

    abstract PayBiz getPayBiz();

    protected void doPayFlow(String tradeNo,Map<String,Object> map) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        if(Objects.isNull(payRequest)) {
            getRequestAdapter().adpat(map);
        }

        payRequest = payRequestDao.selectByTradeNo(tradeNo);
        if(Objects.isNull(payRequest)) {
            throw new BizFailException("处理失败");
        }

        doPayFlow(payRequest);
    }

    public void doPayFlow(PayRequest payRequest) {
        switch (PayRequestStatus.parse(payRequest.getStatus())) {
            case NEW:
                payService.saveOprLog(payRequest);
                doRemotePay(payRequest);
                break;
            case OPR_GENERATED:
                doRemotePay(payRequest);
                break;
            case PROCESSING:
                retryRequest(payRequest);
                break;
        }
    }

    public void doRemotePay(PayRequest payRequest) {
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(payRequest.getOutTradeNo(), OperateType.HF_CLIENT.getValue(), TradeType.PAY.getValue());
        Map<String,Object> map = new Gson().fromJson(payMsgRecord.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        map.put("nonce_str",Utils.getRandomString(8));
        String sign = getSign(map,payMsgRecord.getCipherCode());
        map.put("sign",sign);

        PayMsgRecord hfResultMsg = payMsgRecordDao.selectByTradeNo(payMsgRecord.getOutTradeNo(),OperateType.HF_USER.getValue(),TradeType.PAY.getValue());
        if(!Objects.isNull(hfResultMsg)) {
            return;
        }
        PayMsgRecord resultMsg = payMsgRecordDao.selectByTradeNo(payMsgRecord.getOutTradeNo(),OperateType.CLIENT_HF.getValue(),TradeType.PAY.getValue());

        Map<String,Object> resultMap;
        if(Objects.isNull(resultMsg)) {
            resultMap = getClient().unifiedorder(map);
        } else {
            resultMap = new Gson().fromJson(resultMsg.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        }

        if(null != resultMap) {
            resultMap.put("inputMsg",payMsgRecord);
        }
        getResponseAdapter().adpat(resultMap);
    }

    private void retryRequest(PayRequest payRequest) {
        doRemotePay(payRequest);
    }

    @Override
    public Map<String, Object> pay(Map<String, Object> requestMap) {
        String outTradeNo = String.valueOf(requestMap.get("out_trade_no"));
        String merchant_no = String.valueOf(requestMap.get("merchant_no"));
        String tradeNo = String.format("%s_%s",merchant_no,outTradeNo);

        doPayFlow(tradeNo,requestMap);

        PayMsgRecord result = payMsgRecordDao.selectByTradeNo(tradeNo, OperateType.HF_USER.getValue(), TradeType.PAY.getValue());
        return new Gson().fromJson(result.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public void handleProcessingRequest(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        if(payRequest.getStatus() != PayRequestStatus.PROCESSING.getValue() && payRequest.getStatus() != PayRequestStatus.OPR_SUCCESS.getValue()) {
            logger.warn(String.format("payRequest not processing,%s,%s",payRequest.getOutTradeNo(),payRequest.getStatus()));
            return;
        }

        if(payRequest.getStatus() == PayRequestStatus.PROCESSING.getValue()) {
            Map<String,Object> payResult = query(payRequest);

            if(MapUtils.isEmpty(payResult)) {
                logger.warn(String.format("query failed,%s",payRequest.getOutTradeNo()));
                return;
            }

            if(Objects.isNull(payResult.get("errcode"))) {
                logger.warn(String.format("query failed,errcode is null,%s",payRequest.getOutTradeNo()));
            }

            int errcode = new BigDecimal(String.valueOf(payResult.get("errcode"))).intValue();
            if(errcode != 0) {
                return;
            }
            logger.info(String.format("%s,query result:%s",payRequest.getOutTradeNo(),new Gson().toJson(payResult)));

            String message = String.valueOf(payResult.get("message"));
            String service = String.valueOf(payResult.get("service"));
            String no = String.valueOf(payResult.get("no"));
            String out_trade_no = String.valueOf(payResult.get("out_trade_no"));
            int status = new BigDecimal(String.valueOf(payResult.get("status"))).intValue();

            switch (status) {
                case 0:
                    logger.info(String.format("not paid,%s",payRequest.getOutTradeNo()));
                    return;
                case 1:
                    logger.info(String.format("pay success,%s",payRequest.getOutTradeNo()));
                    payService.paySuccess(payRequest.getOutTradeNo());
                    return;
                case 2:
                    logger.info(String.format("waiting pay,%s",payRequest.getOutTradeNo()));
                    return;
                case 3:
                case 4:
                case 5:
                    logger.info(String.format("pay failed,%s",payRequest.getOutTradeNo()));
                    payService.payFailed(payRequest.getOutTradeNo());
            }
        }
        notice(payRequest);
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        return null;
    }

    @Override
    public void notice(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        if(payRequest.getStatus() != PayRequestStatus.OPR_SUCCESS.getValue()) {
            return;
        }
        payRequestDao.updateNoticeRetryTime(payRequest.getId());

        UserGroup userGroup = userGroupDao.selectByGroupNo(payRequest.getMchId());
        String url = StringUtils.isEmpty(payRequest.getOutNotifyUrl())?userGroup.getCallbackUrl():payRequest.getOutNotifyUrl();
        if(StringUtils.isEmpty(url)) {
            logger.warn(String.format("callback url is null,%s",url));
            payRequestDao.updateNoticeStatus(payRequest.getId());
            return;
        }

        Map<String,Object> resutMap = new HashMap<>();

        if(StringUtils.equalsIgnoreCase(payRequest.getPayResult(),"0")) {
            //code 0成功 99失败
            resutMap.put("errcode","0");
            //msg
            resutMap.put("message","支付成功");

            resutMap.put("no",payRequest.getId());
            //out_trade_no
            resutMap.put("out_trade_no",payRequest.getOutTradeNo().split("_")[1]);
            //mch_id
            resutMap.put("merchant_no",payRequest.getMchId());
            //total
            resutMap.put("total",payRequest.getTotalFee());
            //fee
            resutMap.put("fee",payRequest.getFee());
            //trade_type 1:收单 2:撤销 3:退款
            resutMap.put("trade_type","1");
            //sign_type
            resutMap.put("sign_type","MD5");
            String sign = Utils.encrypt(resutMap,userGroup.getCipherCode());
            resutMap.put("sign",sign);

            boolean result = callBackClient.post(url,resutMap);

            if(result) {
                payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
                payRequestDao.updateNoticeStatus(payRequest.getId());
            }
        } else {
            resutMap.put("errcode","99");
            resutMap.put("message","支付失败");
            resutMap.put("no",payRequest.getId());
            resutMap.put("out_trade_no",payRequest.getOutTradeNo());
            resutMap.put("merchant_no",payRequest.getMchId());
            resutMap.put("trade_type","1");
            resutMap.put("sign_type","MD5");
            String sign = Utils.encrypt(resutMap,userGroup.getCipherCode());
            resutMap.put("sign",sign);

            boolean result = callBackClient.post(url,resutMap);
            if(result) {
                payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
                payRequestDao.updateNoticeStatus(payRequest.getId());
            }
        }
    }

    @Override
    public void finishPay(Map<String, String> map) {

    }
}
