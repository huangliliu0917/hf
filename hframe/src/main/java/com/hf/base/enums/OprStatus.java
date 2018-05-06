package com.hf.base.enums;

public enum OprStatus {
    NEW(0,"未处理"),
    PAY_SUCCESS(1,"成功，未入账"),//代付已提交
    PAY_FAILED(99,"处理失败"),
    FINISHED(10,"成功"),
    REFUND(98,"已退款");

    private int value;
    private String desc;

    OprStatus(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public static OprStatus parse(int value) {
        for(OprStatus oprStatus:OprStatus.values()) {
            if(oprStatus.getValue()== value) {
                return oprStatus;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }
}
