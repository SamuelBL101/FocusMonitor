package com.focusmonitor.client.clientdesktop.modules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Activity {
    private String appName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Activity(String appName, LocalDateTime startTime, LocalDateTime endTime) {

        this.appName = appName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getAppName() { return appName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}
