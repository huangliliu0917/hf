package com.hf.base.utils;

import java.util.List;

public class ShowPage {
    private List<Page> pages;
    private boolean showHead;
    private boolean showTail;

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public boolean getShowHead() {
        return showHead;
    }

    public void setShowHead(boolean showHead) {
        this.showHead = showHead;
    }

    public boolean getShowTail() {
        return showTail;
    }

    public void setShowTail(boolean showTail) {
        this.showTail = showTail;
    }
}
