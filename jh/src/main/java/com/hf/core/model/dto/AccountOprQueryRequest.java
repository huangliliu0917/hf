package com.hf.core.model.dto;

import java.util.Date;

/**
 * Created by tengfei on 2017/11/4.
 */
public class AccountOprQueryRequest {
    private Long userId;
    private String outTradeNo;
    private Date startDate;
    private String channel;
    private Integer oprType;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getOprType() {
        return oprType;
    }

    public void setOprType(Integer oprType) {
        this.oprType = oprType;
    }
}
