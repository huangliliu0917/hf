package com.hf.core.model.po;

import com.google.gson.Gson;

import java.util.Map;

public class PayMsgRecord {
    private Long id;

    private String outTradeNo;

    private String merchantNo;

    private String service;

    private Integer operateType;

    private Integer tradeType;

    private Integer status;

    private String msgBody;

    private String cipherCode;

    public PayMsgRecord() {}

    public PayMsgRecord(String outTradeNo, String merchantNo, String service, Integer operateType, Integer tradeType, Object map) {
        this.outTradeNo = outTradeNo;
        this.merchantNo = merchantNo;
        this.service = service;
        this.operateType = operateType;
        this.tradeType = tradeType;
        this.cipherCode = "";
        this.msgBody = new Gson().toJson(map);
    }

    public PayMsgRecord(String outTradeNo, String merchantNo, String service, Integer operateType, Integer tradeType,String cipherCode, Map<String,Object> map) {
        this.outTradeNo = outTradeNo;
        this.merchantNo = merchantNo;
        this.service = service;
        this.operateType = operateType;
        this.tradeType = tradeType;
        this.cipherCode = cipherCode;
        this.msgBody = new Gson().toJson(map);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service == null ? null : service.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody == null ? null : msgBody.trim();
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public String getCipherCode() {
        return cipherCode;
    }

    public void setCipherCode(String cipherCode) {
        this.cipherCode = cipherCode;
    }
}