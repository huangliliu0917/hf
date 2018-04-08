package com.hf.core.model.po;

import com.hf.core.model.remote.RefundRequest;

import java.util.Date;

public class PayRefundOrder {
    private Long id;

    private String merchantNo;

    private Integer refund;

    private String oriNo;

    private String refundNo;

    private String outNotifyUrl;

    private String nonceStr;

    private String signType;

    private String sign;

    private Integer refundType;

    private Integer status;

    private Integer version;

    private Date createTime;

    private Date updateTime;

    private Integer errcode;

    private String message;

    private String no;

    private Integer actualRefundFee;

    public PayRefundOrder(RefundRequest refundRequest) {
        this.merchantNo = refundRequest.getMerchant_no();
        this.refund = refundRequest.getRefund_fee();
        this.oriNo = refundRequest.getOri_no();
        this.refundNo = refundRequest.getRefund_no();
        this.outNotifyUrl = refundRequest.getOut_notify_url();
        this.nonceStr = refundRequest.getNonce_str();
        this.signType = refundRequest.getSign_type();
        this.sign = refundRequest.getSign();
        this.refundType = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public Integer getRefund() {
        return refund;
    }

    public void setRefund(Integer refund) {
        this.refund = refund;
    }

    public String getOriNo() {
        return oriNo;
    }

    public void setOriNo(String oriNo) {
        this.oriNo = oriNo == null ? null : oriNo.trim();
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo == null ? null : refundNo.trim();
    }

    public String getOutNotifyUrl() {
        return outNotifyUrl;
    }

    public void setOutNotifyUrl(String outNotifyUrl) {
        this.outNotifyUrl = outNotifyUrl == null ? null : outNotifyUrl.trim();
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

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
        this.message = message == null ? null : message.trim();
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no == null ? null : no.trim();
    }

    public Integer getActualRefundFee() {
        return actualRefundFee;
    }

    public void setActualRefundFee(Integer actualRefundFee) {
        this.actualRefundFee = actualRefundFee;
    }
}