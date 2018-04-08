package com.hf.base.utils;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pagenation<T> {
    private int currentPage;
    private int nextPage;
    private int pageSize;
    private int totalPage;
    private int totalSize;
    private List<T> data;
    private ShowPage showPage;
    private Page headPage;
    private Page tailPage;
    private boolean hasNext;

    public Pagenation(List<T> list) {

    }

    public Pagenation(List<T> list,int currentPage,int pageSize) {
        if(CollectionUtils.isEmpty(list)) {
            this.currentPage = 1;
            this.nextPage = 1;
            this.pageSize = pageSize;
            this.totalPage = 1;
            this.totalSize = 0;
            this.data = new ArrayList<>();
        } else {
            List<List<T>> lists = Lists.partition(list,pageSize);
            this.totalSize = list.size();
            this.pageSize = pageSize;
            this.totalPage = lists.size();
            int actualPage = lists.size();
            this.currentPage = actualPage>currentPage?currentPage:actualPage;
            this.nextPage = this.currentPage+1>actualPage?actualPage:this.currentPage+1;
            this.data = lists.get(this.currentPage-1);
        }
        buildShowPage();
    }

    public Pagenation(List<T> list,int totalSize, int currentPage,int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalSize%pageSize==0?totalSize/pageSize:totalSize/pageSize +1;
        if(this.totalPage == 0) {
            this.totalPage = 1;
        }
        this.nextPage = currentPage >= totalPage?totalPage:currentPage+1;
        this.totalSize = totalSize;
        this.data = list;

        buildShowPage();
    }

    private void buildShowPage() {
        List<Page> totalPages = new ArrayList<>();
        for(int i=0;i<totalPage;i++) {
            totalPages.add(new Page(i+1,this.currentPage==i+1));
        }
        List<List<Page>> lists = Lists.partition(totalPages,10);
        if(CollectionUtils.isEmpty(lists)) {
            ShowPage showPage = new ShowPage();
            showPage.setPages(Collections.EMPTY_LIST);
            showPage.setShowTail(false);
            showPage.setShowHead(false);
            this.showPage = showPage;
            return;
        }

        int currentShowPageIndex = this.currentPage%10==0?this.currentPage/10:this.currentPage/10+1;
        int totalShowPageIndex = this.totalPage%10==0?this.totalPage/10:this.totalPage/10+1;

        ShowPage showPage = new ShowPage();
        showPage.setPages(lists.get(currentShowPageIndex-1));
        showPage.setShowHead(currentShowPageIndex!=1);
        showPage.setShowTail(currentShowPageIndex!=totalShowPageIndex);

        this.showPage = showPage;
        this.headPage = new Page(1,"首页",false);
        this.tailPage = new Page(totalPage,"最后一页",false);

        if(this.currentPage == this.totalPage) {
            this.hasNext = false;
        } else {
            this.hasNext = true;
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public ShowPage getShowPage() {
        return showPage;
    }

    public void setShowPage(ShowPage showPage) {
        this.showPage = showPage;
    }

    public Page getHeadPage() {
        return headPage;
    }

    public void setHeadPage(Page headPage) {
        this.headPage = headPage;
    }

    public Page getTailPage() {
        return tailPage;
    }

    public void setTailPage(Page tailPage) {
        this.tailPage = tailPage;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
