package com.hf.base.model;

import com.hf.base.annotations.Field;

public class AccountRequest {
    @Field
    private String mchId;
    @Field
    private String name;
    @Field
    private Integer status;
    @Field
    private Integer groupType;
    @Field(required = true)
    private Long groupId;
    @Field(required = true,defaults = "1")
    private Integer pageSize;
    @Field(required = true,defaults = "100")
    private Integer currentPage;

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
