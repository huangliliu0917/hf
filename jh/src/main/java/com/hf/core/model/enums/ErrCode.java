package com.hf.core.model.enums;

import java.math.BigDecimal;

/**
 * Created by tengfei on 2017/10/28.
 */
public enum ErrCode {

    SUCCESS(0,"交易成功"),
    SIGN_FAILED(1,"验签失败"),
    PARAM_FAILED(2,"参数失败"),
    PAY_FAILED(3,"支付失败"),
    WAITING_PAY(4,"等待用户支付");

    private Integer value;
    private String desc;

    ErrCode(Integer value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return this.value;
    }

    public static boolean equals(String input,ErrCode target) {
        return new BigDecimal(input).intValue() == target.getValue();
    }
}
