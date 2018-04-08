package com.hf.base.enums;

public enum GroupStatus {
    NEW(0,"注册完成"),
    SUBMITED(1,"资料补充完成并提交"),
    AVAILABLE(10,"可用"),
    CANCEL(99,"停用");

    private int value;
    private String desc;

    GroupStatus(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public static GroupStatus parse(int value) {
        for(GroupStatus groupStatus:GroupStatus.values()) {
            if(groupStatus.getValue() == value) {
                return groupStatus;
            }
        }
        return  null;
    }
}
