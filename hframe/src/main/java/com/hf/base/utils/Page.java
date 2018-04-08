package com.hf.base.utils;

public class Page {
    private Integer pageIndex;
    private String label;
    private boolean selected;

    public Page(Integer pageIndex,boolean selected) {
        this.pageIndex = pageIndex;
        this.label = String.valueOf(pageIndex);
        this.selected = selected;
    }

    public Page(Integer pageIndex,String label,boolean selected) {
        this.pageIndex = pageIndex;
        this.label = label;
        this.selected = selected;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
