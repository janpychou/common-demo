package io.sugo.user.group.api.dto;

import java.io.Serializable;
import java.util.List;

public class CreateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private DataConfig dataConfig;
    
    private List<String> ids;
    
    private int count;

    public DataConfig getDataConfig() {
        return dataConfig;
    }

    public void setDataConfig(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
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
}
