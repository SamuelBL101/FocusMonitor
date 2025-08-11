package com.focusmonitor.client.clientdesktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomepageController {

    public Label activeWindowLabel;
    private ActivityTracker activityTracker;
    @FXML
    private Label welcomeLabel;

    public void initialize() {
        // You can set user-specific data here later
        welcomeLabel.setText("Welcome to your Dashboard!");
    }
    public void logout() {
        // Logic to handle logout
        System.out.println("User logged out.");
        // You can redirect to the login screen or perform other actions here
    }

    public void startMonitor(ActionEvent actionEvent) throws InterruptedException {
        if (activityTracker == null){
            this.activityTracker = new ActivityTracker(this);
        } else {
            System.out.println("Monitor is already running.");
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