package com.hf.base.model;

import com.hf.base.annotations.Field;

public class Page {
    @Field(type = Field.Type.number,defaults = "1")
    private Integer currentPage;
    @Field(type= Field.Type.number,defaults = "100")
    private Integer pageSize;

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
}
