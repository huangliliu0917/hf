package com.hf.core.model.po;

import java.math.BigDecimal;
import java.util.Date;

public class AccountDailyLimitSum {
    private Long id;

    private Long groupId;

    private String dateStr;

    private BigDecimal sumLimitAmount;

    private BigDecimal lockAmount;

    private Date createTime;

    private Date updateTime;

    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr == null ? null : dateStr.trim();
    }

    public BigDecimal getSumLimitAmount() {
        return sumLimitAmount;
    }

    public void setSumLimitAmount(BigDecimal sumLimitAmount) {
        this.sumLimitAmount = sumLimitAmount;
    }

    public BigDecimal getLockAmount() {
        return lockAmount;
    }

    public void setLockAmount(BigDecimal lockAmount) {
        this.lockAmount = lockAmount;
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
}