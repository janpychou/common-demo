package io.sugo.user.group.api.dto;

import java.io.Serializable;
import java.util.List;

public class GroupData implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> ids;

    private int count;
    
    private int totalCount;

    public GroupData(List<String> ids, int totalCount){
        this.ids = ids;
        this.count = ids.size();
        this.totalCount = totalCount;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
