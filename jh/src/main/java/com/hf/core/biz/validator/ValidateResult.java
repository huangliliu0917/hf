package com.hf.core.biz.validator;

public class ValidateResult {
    private boolean result;
    private String msg;

    private ValidateResult(boolean result,String msg) {
        this.result = result;
        this.msg = msg;
    }

    public static ValidateResult success() {
        return new ValidateResult(true,"success");
    }

    public static ValidateResult failed(String msg) {
        return new ValidateResult(false,msg);
    }

    public boolean isSuccess() {
        return result;
    }

    public String getMsg() {
        return msg;
    }
}
