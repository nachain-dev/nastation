package org.nastation.common.model;

import java.util.List;

public class PageWrap<T> {
    private List<T> dataList;
    private int pageNumber;
    private int pageSize;
    private int pageTotal;
    private long dataTotal;

    public PageWrap() {
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public long getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(long dataTotal) {
        this.dataTotal = dataTotal;
    }
}
