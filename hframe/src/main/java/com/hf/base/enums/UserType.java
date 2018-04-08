package com.hf.base.enums;

public enum UserType {
    ADMIN(1,"管理员"),
    CUSTOMER(3,"普通用户"),
    SUPER_ADMIN(10,"超级管理员");

    private int value;
    private String desc;

    UserType(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

    public static UserType parse(int value) {
        for(UserType type:UserType.values()) {
            if(type.value == value) {
                return type;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
        }

    public int getValue() {
        return this.value;
    }
}