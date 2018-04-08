package com.hf.core.biz;

import com.hf.base.model.*;
import com.hf.base.utils.Pagenation;
import com.hf.core.model.dto.AccountOprQueryRequest;
import com.hf.core.model.dto.AccountOprQueryResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tengfei on 2017/11/4.
 */
public interface AccountBiz {
    List<AccountOprQueryResponse> getAccountOprLogs(AccountOprQueryRequest request);
    Pagenation<AccountPageInfo> getAccountPage(AccountRequest accountRequest);
    Pagenation<AdminAccountPageInfo> getAdminAccountPage(AccountRequest accountRequest);
    Pagenation<AccountOprInfo> getAccountOprPage(AccountOprRequest accountOprRequest);
    BigDecimal getLockedAmount(Long groupId);
}
