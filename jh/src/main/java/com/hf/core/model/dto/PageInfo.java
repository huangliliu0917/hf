package com.hf.core.model.dto;

import com.hf.core.model.po.PageLayOut;

import java.util.List;

/**
 * Created by tengfei on 2017/10/29.
 */
public class PageInfo {

    private PageLayOut pageLayOut;

    private List<PageLayOut> subList;

    public PageLayOut getPageLayOut() {
        return pageLayOut;
    }

    public void setPageLayOut(PageLayOut pageLayOut) {
        this.pageLayOut = pageLayOut;
    }

    public List<PageLayOut> getSubList() {
        return subList;
    }

    public void setSubList(List<PageLayOut> subList) {
        this.subList = subList;
    }
}