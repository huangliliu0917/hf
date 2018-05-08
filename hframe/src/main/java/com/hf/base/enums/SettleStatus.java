package com.hf.base.enums;

public enum SettleStatus {
    NEW(0,"未处理"),PROCESSING(1,"处理中"),AGENT_PAY(2,"代付中"),SUCCESS(10,"处理完成"),FAILED(99,"处理失败");

    private int value;
    private String desc;

    SettleStatus(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static SettleStatus parse(int value) {
        for(SettleStatus settleStatus:SettleStatus.values()) {
            if(settleStatus.getValue() == value) {
                return settleStatus;
            }
        }
        return null;
    }
}
