package com.hf.base.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

public class DefaultDispatcherFactory implements DispatcherFactory {
    @Autowired
    @Qualifier("defaultDispatcher")
    private Dispatcher defaultDispatcher;
    private Map<String,Dispatcher> dispatcherMap;

    public DefaultDispatcherFactory(Map<String,Dispatcher> dispatcherMap) {
        this.dispatcherMap = dispatcherMap;
    }

    @Override
    public Dispatcher getDispatcher(HttpServletRequest request, String page) {
        Dispatcher dispatcher = dispatcherMap.get(page);
        if(Objects.isNull(dispatcherMap.get(page))) {
            return defaultDispatcher;
        } else {
            return dispatcher;
        }
    }
}
