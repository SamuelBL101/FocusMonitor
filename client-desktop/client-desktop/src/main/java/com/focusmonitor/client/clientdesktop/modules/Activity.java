package com.focusmonitor.client.clientdesktop.modules;

public class Activity {
    private String appName;
    private String startTime;
    private String endTime;

    public Activity(String appName, String startTime, String endTime) {
        this.appName = appName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getAppName() { return appName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}
