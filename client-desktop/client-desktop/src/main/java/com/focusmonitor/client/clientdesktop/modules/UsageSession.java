package com.focusmonitor.client.clientdesktop.modules;

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
        return "{ \"userId\": " + userId + ", " +
                "\"activity\": { " +
                "\"appName\": \"" + activity.getAppName() + "\", " +
                "\"startTime\": \"" + activity.getStartTime() + "\", " +
                "\"endTime\": \"" + activity.getEndTime() + "\" } }";
    }
}
