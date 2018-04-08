package com.hf.base.dispatcher;

import javax.servlet.http.HttpServletRequest;

public interface Dispatcher {
    DispatchResult dispatch(HttpServletRequest request, String page);
}
