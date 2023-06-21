package org.nastation.module.pub.data;

public class ProcessInfo {
    private long id;
    private long instanceId;
    private long currentHeight;
    private long lastBlockHeight;

    private String percent;

    public ProcessInfo() {
    }

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }

    public long getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(long currentHeight) {
        this.currentHeight = currentHeight;
    }

    public long getLastBlockHeight() {
        return lastBlockHeight;
    }

    public void setLastBlockHeight(long lastBlockHeight) {
        this.lastBlockHeight = lastBlockHeight;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
