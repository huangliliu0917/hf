package com.hf.base.model;

import java.math.BigDecimal;
import java.util.Date;

public class UserChannel {
    private Long id;

    private String channelName;

    private String channelCode;

    private Long channelId;

    private Long groupId;

    private String groupName;

    private BigDecimal feeRate;

    private BigDecimal standardFeeRate;

    private String mchId;

    private String cipherCode;

    private String callbackUrl;

    private Integer status;

    private Integer version;

    private Date createTime;

    private Date updateTime;

    private Long subGroupId;

    private Long companyId;

    private BigDecimal subFeeRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getStandardFeeRate() {
        return standardFeeRate;
    }

    public void setStandardFeeRate(BigDecimal standardFeeRate) {
        this.standardFeeRate = standardFeeRate;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId == null ? null : mchId.trim();
    }

    public String getCipherCode() {
        return cipherCode;
    }

    public void setCipherCode(String cipherCode) {
        this.cipherCode = cipherCode == null ? null : cipherCode.trim();
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl == null ? null : callbackUrl.trim();
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

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Long getSubGroupId() {
        return subGroupId;
    }

    public void setSubGroupId(Long subGroupId) {
        this.subGroupId = subGroupId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getSubFeeRate() {
        return subFeeRate;
    }

    public void setSubFeeRate(BigDecimal subFeeRate) {
        this.subFeeRate = subFeeRate;
    }
}