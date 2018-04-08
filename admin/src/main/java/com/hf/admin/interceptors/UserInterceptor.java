package com.hf.admin.interceptors;

import com.hf.base.interceptors.LoginInterceptor;

public class UserInterceptor  extends LoginInterceptor {
    @Override
    public String getSystem() {
        return "admin";
    }
}
