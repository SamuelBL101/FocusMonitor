package com.focusmonitor.client.clientdesktop.modules;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class UsageSession {
    private String userId;
    private Activity activity;

    public UsageSession(String userId, Activity activity) {
        this.userId = userId;
        this.activity = activity;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}
