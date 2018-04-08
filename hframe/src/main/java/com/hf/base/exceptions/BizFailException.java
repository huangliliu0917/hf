package com.hf.base.exceptions;

public class BizFailException extends RuntimeException {

    private String code;
    private String message;

    public BizFailException() {
    }

    public BizFailException(String message) {
        super(message);
        this.message = message;
    }

    public BizFailException(String code,String message) {
        super(String.format("%s:%s",code,message));
        this.code = code;
        this.message = message;
    }

    public BizFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizFailException(Throwable cause) {
        super(cause);
    }

    public BizFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
