package com.hf.user.service;

import com.hf.base.contants.Constants;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.utils.MapUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserAddBankCardDispatcher implements Dispatcher {

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.setData(MapUtils.buildMap("bankNames", Constants.bankNames));
        return dispatchResult;
    }
}
