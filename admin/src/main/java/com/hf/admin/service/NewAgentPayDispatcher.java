package com.hf.admin.service;

import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class NewAgentPayDispatcher implements Dispatcher {

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String withdrawId = request.getParameter("id");

    }
}
