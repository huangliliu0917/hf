package com.hf.core.model.po;

import com.hf.core.model.dto.PayRequestDto;

import java.math.BigDecimal;
import java.util.Date;

public class PayRequest {
    private Long id;

    private String outTradeNo;

    private Integer totalFee;

    private BigDecimal fee;

    private BigDecimal actualAmount;

    private String body;

    private String mchId;

    private String subOpenid;

    private String buyerId;

    private String service;

    private String appid;

    private String sign;

    private Integer status;

    private String payResult;

    private Date createTime;

    private Date updateTime;

    private Integer version;

    private Integer tradeType;

    private String remark;

    private String channelProviderCode;

    private String outNotifyUrl;

    private int noticeRetryTime;

    private int noticeStatus;

    public PayRequest() {

    }

    public PayRequest(PayRequestDto request) {
        this.outTradeNo = request.getOut_trade_no();
        this.body = request.getBody();
        this.mchId = request.getMch_id();
        this.subOpenid = request.getSub_openid();
        this.buyerId = request.getBuyer_id();
        this.service = request.getService();
        this.appid = request.getAppid();
        this.sign = request.getSign();
        this.totalFee = request.getTotal_fee();
    }

//    public PayRequest

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId == null ? null : mchId.trim();
    }

    public String getSubOpenid() {
        return subOpenid;
    }

    public void setSubOpenid(String subOpenid) {
        this.subOpenid = subOpenid == null ? null : subOpenid.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId == null ? null : buyerId.trim();
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service == null ? null : service.trim();
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getChannelProviderCode() {
        return channelProviderCode;
    }

    public void setChannelProviderCode(String channelProviderCode) {
        this.channelProviderCode = channelProviderCode;
    }

    public String getPayResult() {
        return payResult;
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }

    public String getOutNotifyUrl() {
        return outNotifyUrl;
    }

    public void setOutNotifyUrl(String outNotifyUrl) {
        this.outNotifyUrl = outNotifyUrl;
    }

    public int getNoticeRetryTime() {
        return noticeRetryTime;
    }

    public void setNoticeRetryTime(int noticeRetryTime) {
        this.noticeRetryTime = noticeRetryTime;
    }

    public int getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(int noticeStatus) {
        this.noticeStatus = noticeStatus;
    }
}