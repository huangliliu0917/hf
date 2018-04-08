package com.hf.core.model.po;

import com.hf.core.model.dto.ReverseRequest;

import java.util.Date;

public class PayReverseOrder {
    private Long id;

    private Long extId;

    private String merchantNo;

    private String no;

    private String outTradeNo;

    private String nonceStr;

    private String signType;

    private String sign;

    private Integer status;

    private String version;

    private Date createTime;

    private Date updateTime;

    public PayReverseOrder(ReverseRequest reverseRequest) {
        this.merchantNo = reverseRequest.getMerchant_no();
        this.no = reverseRequest.getNo();
        this.outTradeNo = reverseRequest.getOut_trade_no();
        this.nonceStr = reverseRequest.getNonce_str();
        this.signType = reverseRequest.getSign_type();
        this.sign = reverseRequest.getSign();
        this.version = reverseRequest.getVersion();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExtId() {
        return extId;
    }

    public void setExtId(Long extId) {
        this.extId = extId;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no == null ? null : no.trim();
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr == null ? null : nonceStr.trim();
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType == null ? null : signType.trim();
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public interface STATUS {
        int SUCCESS = 0;
        int CHECK_FAILED = 1;
        int PARAM_FAILED = 2;
        int REVERSE_FAILED = 3;
        int INIT = 10;
    }
}