package com.hf.base.model;

import com.hf.base.annotations.Field;

public class WithDrawRequest {
    @Field(required = true)
    private Long groupId;
    @Field
    private String mchId;
    @Field
    private Integer status;
    @Field(required = true,defaults = "1")
    private Integer currentPage;
    @Field(required = true,defaults = "15")
    private Integer pageSize;
    @Field(required = true,defaults = "0")
    private Integer role;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
