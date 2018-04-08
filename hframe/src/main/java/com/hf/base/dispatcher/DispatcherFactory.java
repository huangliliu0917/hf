package com.hf.base.dispatcher;

import javax.servlet.http.HttpServletRequest;

public interface DispatcherFactory {
    Dispatcher getDispatcher(HttpServletRequest request, String page);
}
