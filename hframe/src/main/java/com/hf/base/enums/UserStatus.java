package com.hf.base.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserStatus {
    NEW(0,"注册完成"),
    SUBMITED(1,"资料补充完成并提交"),
    AVAILABLE(10,"可用"),
    CANCEL(99,"停用");

    private int value;
    private String desc;

    UserStatus(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserStatus parse(String value) {
        for(UserStatus userStatus:UserStatus.values()) {
            if(StringUtils.equals(String.valueOf(userStatus.value),value)) {
                return userStatus;
            }
        }
        return null;
    }

    public static UserStatus parse(Integer value) {
        for(UserStatus userStatus:UserStatus.values()) {
            if(userStatus.value == value) {
                return userStatus;
            }
        }
        return null;
    }

    public int getValue() {
        return this.value;
    }
}
