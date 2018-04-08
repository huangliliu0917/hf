package com.hf.core.model.po;

import java.math.BigDecimal;

public class UserFeeRate {
    private Long id;

    private Long channelId;

    private String channel;

    private Long userId;

    private BigDecimal userFeeRate;

    private Integer status;

    private Long creater;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getUserFeeRate() {
        return userFeeRate;
    }

    public void setUserFeeRate(BigDecimal userFeeRate) {
        this.userFeeRate = userFeeRate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreater() {
        return creater;
    }

    public void setCreater(Long creater) {
        this.creater = creater;
    }
}