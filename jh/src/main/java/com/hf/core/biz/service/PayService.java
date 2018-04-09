package com.hf.core.biz.service;

import com.hf.core.model.po.AdminBankCard;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface PayService {
    void saveOprLog(PayRequest payRequest);
    void remoteSuccess(PayRequest payRequest, PayRequestBack payRequestBack);
    void remoteSuccess(PayRequest payRequest, PayMsgRecord hfResultMsg);
    void payFailed(String outTradeNo, PayMsgRecord hfResultMsg);
    void payFailed(String outTradeNo, PayRequestBack payRequestBack);
    void paySuccess(String outTradeNo);
    void payFailed(String outTradeNo);
    void payPromote(String outTradeNo);
    void savePayMsg(List<PayMsgRecord> records);
    Map<Long,BigDecimal> chooseAdminBank(Long groupId, BigDecimal amount);
    AdminBankCard chooseAdminBank1(Long groupId, BigDecimal amount);
    void savePayRequest(List<PayMsgRecord> msgRecords, PayRequest payRequest);
    void updateDailyLimit();
    void savePayMsg(PayMsgRecord payMsgRecord);
    void startRefund(PayRequest payRequest, PayMsgRecord payMsgRecord);
    void finishRefund(PayRequest payRequest);
}
