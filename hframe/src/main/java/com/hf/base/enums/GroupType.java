package com.hf.base.enums;

public enum GroupType {
    CUSTOMER(1,"普通商户"),
    AGENT(2,"代理商"),
    COMPANY(3,"分公司"),
    SALES(4,"业务员"),
    SUPER(10,"总部");

    private int value;
    private String desc;

    GroupType(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public static GroupType parse(int value) {
        for (GroupType groupType : GroupType.values()) {
            if (groupType.value == value) {
                return groupType;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }
}
