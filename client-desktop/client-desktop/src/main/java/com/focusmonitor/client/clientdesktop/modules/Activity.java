package com.focusmonitor.client.clientdesktop.modules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Activity {
    private String appName;
    private String windowTitle;

    public Activity(String appName, String windowTitle) {

        this.appName = appName;
        this.windowTitle = windowTitle;

    }

    public String getAppName() { return appName; }
    public String getWindowTitle() { return  windowTitle;};


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(appName, activity.appName) && Objects.equals(windowTitle, activity.windowTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, windowTitle);
    }
}
