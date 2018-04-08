package com.hf.core.model.enums;

/**
 * Created by tengfei on 2017/11/7.
 */
public enum Auth {
    INIT(0,"待完善信息!"),PROCESSING(1,"审核中!"),AVAILABLE(2,"您已成功认证！"),FAILED(3,"审核驳回!");

    private int value;
    private String desc;

    Auth(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static Auth parse(int value) {
        for(Auth auth: Auth.values()) {
            if(value == auth.value) {
                return auth;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
    }
}
