package io.sugo.user.group.api.dto;

import java.io.Serializable;

public class ReadDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private DataConfig dataConfig;
    
    private GroupReadConfig groupReadConfig;
    

    public DataConfig getDataConfig() {
        return dataConfig;
    }

    public void setDataConfig(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
    }
    
    public GroupReadConfig getGroupReadConfig() {
        return groupReadConfig;
    }
    
    public void setGroupReadConfig(GroupReadConfig groupReadConfig) {
        this.groupReadConfig = groupReadConfig;
    }
}
