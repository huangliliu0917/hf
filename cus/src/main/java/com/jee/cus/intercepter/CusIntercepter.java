package com.jee.cus.intercepter;

import com.hf.base.interceptors.LoginInterceptor;

public class CusIntercepter extends LoginInterceptor {
    public String getSystem() {
        return "user";
    }
}
