package com.hf.base.model;

import java.math.BigDecimal;
import java.util.List;

public class UserStatistic {
    private Long groupId;
    private String groupNo;
    private String groupName;
    private BigDecimal amount;
    private List<UserStatisticDetail> list;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<UserStatisticDetail> getList() {
        return list;
    }

    public void setList(List<UserStatisticDetail> list) {
        this.list = list;
    }
}
