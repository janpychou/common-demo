package io.sugo.user.group.api.dto;

import java.io.Serializable;

public class DataConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    
    private String hostAndPorts;
    
    private boolean clusterMode;
    
    private String groupId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostAndPorts() {
        return hostAndPorts;
    }

    public void setHostAndPorts(String hostAndPorts) {
        this.hostAndPorts = hostAndPorts;
    }

    public boolean getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(boolean clusterMode) {
        this.clusterMode = clusterMode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
}
