package com.hf.core.model.po;

import com.hf.base.annotations.Field;

import java.math.BigDecimal;
import java.util.Date;

public class SettleTask {
    private Long id;
    @Field(required = true)
    private Long groupId;

    private Long accountId;
    @Field(required = true)
    private Long settleBankCard;
    @Field(required = true)
    private BigDecimal settleAmount;
    @Field(required = true)
    private String bank;
    @Field(required = true)
    private String deposit;
    @Field(required = true)
    private String bankNo;
    @Field(required = true)
    private String owner;

    private BigDecimal feeRate;

    private Long payAccountId;

    private Long payGroupId;

    private Long payBankCard;

    private BigDecimal fee;

    private BigDecimal payAmount;

    private Integer status;

    private Integer version;

    private Date createTime;

    private Date updateTime;

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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getSettleBankCard() {
        return settleBankCard;
    }

    public void setSettleBankCard(Long settleBankCard) {
        this.settleBankCard = settleBankCard;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public Long getPayAccountId() {
        return payAccountId;
    }

    public void setPayAccountId(Long payAccountId) {
        this.payAccountId = payAccountId;
    }

    public Long getPayGroupId() {
        return payGroupId;
    }

    public void setPayGroupId(Long payGroupId) {
        this.payGroupId = payGroupId;
    }

    public Long getPayBankCard() {
        return payBankCard;
    }

    public void setPayBankCard(Long payBankCard) {
        this.payBankCard = payBankCard;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
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

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}