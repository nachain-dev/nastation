package org.nastation.module.pub.data;

public class ChangelogItem {

    private String version;
    private String date;
    private String log;

    public ChangelogItem() {
    }

    public ChangelogItem(String version, String date, String log) {
        this.version = version;
        this.date = date;
        this.log = log;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}