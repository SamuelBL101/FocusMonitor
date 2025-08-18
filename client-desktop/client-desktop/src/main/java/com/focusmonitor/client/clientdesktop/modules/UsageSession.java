package com.focusmonitor.client.clientdesktop.modules;

import java.time.format.DateTimeFormatter;

public class UsageSession {
    private Long userId;
    private Activity activity;

    public UsageSession(Long userId, Activity activity) {
        this.userId = userId;
        this.activity = activity;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String toJson() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String start = activity.getStartTime().format(formatter);
        String end = activity.getEndTime().format(formatter);

        return "{ " +
                "\"user\": { \"id\": " + userId + " }, " +
                "\"applicationName\": \"" + activity.getAppName() + "\", " +
                "\"startTime\": \"" + start + "\", " +
                "\"endTime\": \"" + end + "\" " +
                "}";
    }

}
