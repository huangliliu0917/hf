package com.hf.base.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by tengfei on 2017/11/6.
 */
public class ResponseResult<T> {

    private static final String SUCCESS_CODE = "0000000";
    private static final String SUCCESS_MSG = "SUCCESS";

    private String code;
    private String msg;

    private T data;

    public static <T> ResponseResult success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(SUCCESS_MSG);
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> failed(String errorCode,String errorMsg,T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(errorCode);
        result.setMsg(errorMsg);
        result.setData(data);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return StringUtils.equals(SUCCESS_CODE,this.code);
    }
}
