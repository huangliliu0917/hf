package com.hf.core.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {

    @Value("#{commonConfig[callbackurl]}")
    private String callbackUrl;
    @Value("#{commonConfig[wwPayCallbackUrl]}")
    private String wwCallbackUrl;
    @Value("#{commonConfig[outNotifyLimit]}")
    private Integer outNotifyLimit;

    private String hefuCallBackUrl;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getWwCallbackUrl() {
        return wwCallbackUrl;
    }

    public Integer getOutNotifyLimit() {
        return outNotifyLimit;
    }

    public String getHefuCallBackUrl() {
        return hefuCallBackUrl;
    }
}
