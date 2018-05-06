package com.hf.core.biz.service;

import com.hf.core.model.dto.RefundResponse;
import com.hf.core.model.dto.ReverseResponse;
import com.hf.core.model.po.AccountOprLog;
import com.hf.core.model.po.SettleTask;

/**
 * Created by tengfei on 2017/10/28.
 */
public interface AccountService {
//    void pay(PayTrdOrder payTrdOrder);
    void refund(RefundResponse refundResponse);
    void reverse(ReverseResponse reverseResponse);
    void promote(AccountOprLog log);
    void settle(SettleTask settleTask);
    void adminSettle(SettleTask settleTask);
}
