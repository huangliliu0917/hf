package com.hf.core.biz.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.OperateType;
import com.hf.base.enums.TradeType;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.biz.PayBiz;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.*;
import com.hf.core.dao.remote.CallBackClient;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractPayBiz implements PayBiz {

    protected Logger logger = LoggerFactory.getLogger(AbstractPayBiz.class);
    @Autowired
    protected PayRequestDao payRequestDao;
    @Autowired
    private PayService payService;
    @Autowired
    protected PayMsgRecordDao payMsgRecordDao;
    @Autowired
    protected CacheService cacheService;
    @Autowired
    protected UserChannelDao userChannelDao;
    @Autowired
    protected ChannelDao channelDao;
    @Autowired
    private CallBackClient callBackClient;
    @Autowired
    private UserGroupDao userGroupDao;

    private static final String[] needFields = {"service","version","merchant_no","total","name","remark","out_trade_no",
            "create_ip","nonce_str","sign","sign_type"};

    abstract PayClient getPayClient();
    abstract void handlePayResult(PayRequest payRequest,PayMsgRecord payMsgRecord,Map<String, Object> payResult);

    public void pay(PayRequest payRequest) {
//        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
//
//        switch (PayRequestStatus.parse(payRequest.getStatus())) {
//            case NEW:
//                payService.saveOprLog(payRequest);
//                doRemoteCall(payRequest);
//                break;
//            case OPR_GENERATED:
//                doRemoteCall(payRequest);
//                break;
//            case PROCESSING:
//                break;
//            case PAY_FAILED:
//                //失败
//                break;
//            case OPR_SUCCESS:
//                //收到回复消息
//                notice(payRequest);
//                break;
//            case PAY_SUCCESS:
//                notice(payRequest);
//                break;
//            case USER_NOTIFIED:
//
//                break;
//            case OPR_FINISHED:
//                break;
//        }
    }

    @Override
    public void doRemoteCall(PayRequest payRequest) {
        PayMsgRecord payMsgRecord = payMsgRecordDao.selectByTradeNo(payRequest.getOutTradeNo(), OperateType.HF_CLIENT.getValue(), TradeType.PAY.getValue());
        Map<String,Object> params = new Gson().fromJson(payMsgRecord.getMsgBody(),new TypeToken<Map<String,Object>>(){}.getType());
        Map<String,Object> result = getPayClient().unifiedorder(params);
        handlePayResult(payRequest,payMsgRecord,result);
    }

//    @Override
//    public void finishPay() {
//        List<PayRequest> unfinishedRequests = payRequestDao.selectUnfinishedList(DateUtils.addMinutes(new Date(),-0));
//        unfinishedRequests.parallelStream().forEach(payRequest -> {
//            Map<String,String> result = ysClient.getPayResult(payRequest.getMchId(),payRequest.getOutTradeNo());
//            if(org.apache.commons.collections.MapUtils.isEmpty(result)) {
//                return;
//            }
//            if(!StringUtils.equals(result.get("out_trade_no"),payRequest.getOutTradeNo())) {
//                return;
//            }
//            if(StringUtils.equalsIgnoreCase(result.get("trade_state"),"SUCCESS")) {
//                payService.paySuccess(payRequest.getOutTradeNo());
//            }
//        });
//    }

//    @Override
//    public void promote() {
//        List<PayRequest> list = payRequestDao.selectWaitingPromote();
//        list.parallelStream().forEach(payRequest ->
//                payService.payPromote(payRequest.getOutTradeNo())
//        );
//    }

    protected void checkField(Map<String,Object> map) {
        for(String field:needFields) {
            if(Objects.isNull(map.get(field)) || StringUtils.isBlank(map.get(field).toString()) || StringUtils.equalsIgnoreCase("null",map.get(field).toString())) {
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,String.format("参数错误:%s",field));
            }
        }
    }
}
