package com.focusmonitor.client.clientdesktop.controller;

import com.focusmonitor.client.clientdesktop.modules.ActivityTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.http.HttpClient;

public class HomepageController {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    public Label activeWindowLabel;
    private ActivityTracker activityTracker;
    @FXML
    private Label welcomeLabel;

    public void initialize() {
        welcomeLabel.setText("Welcome to your Dashboard!");
    }
    public void logout() {
        System.out.println("User logged out.");
    }

    public void startMonitor(ActionEvent actionEvent) throws InterruptedException {
        if (activityTracker != null && activityTracker.isRunning()) {
            return;
        }
        if (activityTracker == null){
            this.activityTracker = new ActivityTracker(this);
        }
        Thread t = new Thread(activityTracker);
        t.setDaemon(true);
        t.start();

    }

    public void updateActivity(String currentActivity) {
        javafx.application.Platform.runLater(() -> {
            activeWindowLabel.setText(currentActivity);
        });    }


}