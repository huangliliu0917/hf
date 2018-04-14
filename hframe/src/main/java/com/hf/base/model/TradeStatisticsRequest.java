package com.hf.base.model;

import com.hf.base.annotations.Field;

import java.util.Date;

/**
 *交易统计信息查询查询条件类
 */
public class TradeStatisticsRequest {

    @Field
    private String createTime;  //创建时间
    @Field
    private String createTime2;
    @Field
    private String groupNo;   //商户编号

    @Field(required = true)
    private Long groupId;
    @Field(type = Field.Type.number, defaults = "1", required = true)
    private Integer currentPage;
    @Field(type = Field.Type.number, defaults = "100", required = true)
    private Integer pageSize;
    @Field
    private Integer containsSub = 0;
    @Field
    private Integer admin = 0;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public String getCreateTime2() {
        return createTime2;
    }

    public void setCreateTime2(String createTime2) {
        this.createTime2 = createTime2;
    }

    public Integer getContainsSub() {
        return containsSub;
    }

    public void setContainsSub(Integer containsSub) {
        this.containsSub = containsSub;
    }

    public Integer getAdmin() {
        return admin;
    }

    public void setAdmin(Integer admin) {
        this.admin = admin;
    }
}
