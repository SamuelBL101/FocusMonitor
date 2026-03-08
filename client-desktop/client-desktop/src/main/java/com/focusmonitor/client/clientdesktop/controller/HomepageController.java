package com.focusmonitor.client.clientdesktop.controller;

import com.focusmonitor.client.clientdesktop.modules.ActivityTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.prefs.Preferences;

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
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        if (activityTracker != null) {
            activityTracker.stopRunning();
        }
        prefs.remove("jwtToken");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/focusmonitor/client/clientdesktop/welcomepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) activeWindowLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("LoginPage");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        });
    }



}