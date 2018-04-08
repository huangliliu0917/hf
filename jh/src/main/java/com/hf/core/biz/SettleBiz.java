package com.hf.core.biz;

import com.hf.base.model.WithDrawInfo;
import com.hf.base.model.WithDrawRequest;
import com.hf.base.utils.Pagenation;
import com.hf.core.model.po.SettleTask;

public interface SettleBiz {
    void saveSettle(SettleTask settleTask);
    void finishSettle(Long id);
    void settleFailed(Long id);
    Pagenation<WithDrawInfo> getWithDrawPage(WithDrawRequest withDrawRequest);
}
