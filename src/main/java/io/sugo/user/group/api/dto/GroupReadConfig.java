package io.sugo.user.group.api.dto;

import java.io.Serializable;

public class GroupReadConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static int DEFAULT_PAGE_SIZE = 100;
    private Integer pageSize;

    private Integer pageIndex;

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        if (pageIndex == null || pageIndex < 1) {
            return 1;
        }
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getStrartPos() {
        return (getPageIndex() - 1) * getPageSize();
    }

    public int getEndPos() {
        return getPageIndex() * getPageSize();
    }
}
