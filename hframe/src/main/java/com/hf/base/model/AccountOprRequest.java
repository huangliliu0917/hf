package com.hf.base.model;

import com.hf.base.annotations.Field;

public class AccountOprRequest {
    @Field
    private String name;
    @Field
    private String outTradeNo;
    @Field
    private Integer oprType;
    @Field
    private Integer status;
    @Field(required = true,defaults = "1")
    private Integer currentPage;
    @Field(required = true,defaults = "15")
    private Integer pageSize;
    @Field(required = true)
    private Long groupId;

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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public Integer getOprType() {
        return oprType;
    }

    public void setOprType(Integer oprType) {
        this.oprType = oprType;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
