package com.hf.base.model;

import java.math.BigDecimal;
import java.util.Date;

public class AccountPageInfo {
    private Long id;
    private Long groupId;
    private String mchId;
    private String name;
    private BigDecimal amount;
    private BigDecimal lockAmount;
    private BigDecimal processingAmount;
    private BigDecimal totalAmount;
    private BigDecimal totalSettleAmount;
    private BigDecimal totalFee;
    private Date createTime;
    private Date updateTime;
    private Integer groupType;
    private String groupTypeDesc;
    private Integer status;
    private String statusDesc;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getLockAmount() {
        return lockAmount;
    }

    public void setLockAmount(BigDecimal lockAmount) {
        this.lockAmount = lockAmount;
    }

    public BigDecimal getProcessingAmount() {
        return processingAmount;
    }

    public void setProcessingAmount(BigDecimal processingAmount) {
        this.processingAmount = processingAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalSettleAmount() {
        return totalSettleAmount;
    }

    public void setTotalSettleAmount(BigDecimal totalSettleAmount) {
        this.totalSettleAmount = totalSettleAmount;
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

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public String getGroupTypeDesc() {
        return groupTypeDesc;
    }

    public void setGroupTypeDesc(String groupTypeDesc) {
        this.groupTypeDesc = groupTypeDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }
}
