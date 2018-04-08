package com.hf.core.model.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tengfei on 2017/10/28.
 */
public class PayResponse {
    private String out_trade_no;
    private String no;
    private String code_url;
    private String pay_info;
    private Integer total;
    private String transaction_id;
    private Integer paytime;
    private String sign_type;
    private String sign;
    private Integer errcode;
    private String message;

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getCode_url() {
        return code_url;
    }

    public void setCode_url(String code_url) {
        this.code_url = code_url;
    }

    public String getPay_info() {
        return pay_info;
    }

    public void setPay_info(String pay_info) {
        this.pay_info = pay_info;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Integer getPaytime() {
        return paytime;
    }

    public void setPaytime(Integer paytime) {
        this.paytime = paytime;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String,Object> getParams() {
        Map<String,Object> map = new HashMap<>();
        map.put("outTradeNo",this.out_trade_no);
        map.put("no",this.no);
        map.put("codeUrl",this.code_url);
        map.put("payInfo",this.pay_info);
        map.put("actualTotal",this.total);
        map.put("transactionId",this.transaction_id);
        map.put("paytime",paytime);
        map.put("signType",sign_type);
        map.put("sign",sign);
        map.put("errcode",this.errcode);
        map.put("message",this.message);
        return map;
    }
}
