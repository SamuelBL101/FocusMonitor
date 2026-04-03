package com.focusmonitor.client.clientdesktop.controller;

import com.focusmonitor.client.clientdesktop.AppConfig;
import com.focusmonitor.client.clientdesktop.communication.UsageSender;
import com.focusmonitor.client.clientdesktop.modules.ActivityTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.Preferences;

public class HomepageController {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Label activeWindowLabel;
    private ActivityTracker activityTracker;
    private Thread activityTrackerThread;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label activityTitle1;
    @FXML
    private Label activityCopy1;
    @FXML
    private Label activityTime1;
    @FXML
    private Label activityTitle2;
    @FXML
    private Label activityCopy2;
    @FXML
    private Label activityTime2;
    @FXML
    private Label activityTitle3;
    @FXML
    private Label activityCopy3;
    @FXML
    private Label activityTime3;

    public void initialize() {
        welcomeLabel.setText("Welcome to your Dashboard!");
        loadTodaySessions();
    }

    public void logout() {
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        if (activityTracker != null) {
            activityTracker.stopRunning();
        }

        if (activityTrackerThread != null && activityTrackerThread.isAlive()) {
            try {
                activityTrackerThread.join(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        UsageSender.endSession();
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
        activityTrackerThread = new Thread(activityTracker);
        activityTrackerThread.setDaemon(true);
        activityTrackerThread.start();

    }

    public void updateActivity(String currentActivity) {
        javafx.application.Platform.runLater(() -> {
            activeWindowLabel.setText(currentActivity);
        });
    }

    @FXML
    public void refreshTodaySummary(ActionEvent actionEvent) {
        showRow(1, "Refreshing...", "Loading latest summary from server.", "-");
        showRow(2, "-", "-", "-");
        showRow(3, "-", "-", "-");
        loadTodaySessions();
    }

    private void loadTodaySessions() {
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        String token = prefs.get("jwtToken", null);
        if (token == null || token.isBlank()) {
            showRow(1, "No token", "Please sign in again.", "-");
            showRow(2, "-", "-", "-");
            showRow(3, "-", "-", "-");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getBaseURL() + "/api/activitysession/today-summary"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200) {
                        javafx.application.Platform.runLater(() -> {
                            showRow(1, "Load failed", "HTTP " + response.statusCode(), "-");
                            showRow(2, "-", "-", "-");
                            showRow(3, "-", "-", "-");
                        });
                        return;
                    }

                    List<SessionRow> rows = parseRows(response.body());
                    javafx.application.Platform.runLater(() -> renderRows(rows));
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> {
                        showRow(1, "Connection error", ex.getClass().getSimpleName(), "-");
                        showRow(2, "-", "-", "-");
                        showRow(3, "-", "-", "-");
                    });
                    return null;
                });
    }

    private List<SessionRow> parseRows(String body) {
        JSONArray array = new JSONArray(body);
        List<SessionRow> rows = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            String title = item.optString("appName", "Unknown app");
            int totalSeconds = item.optInt("totalSeconds", 0);
            int sessionsCount = item.optInt("sessionsCount", 0);
            String copy = "Sessions: " + sessionsCount;
            String timeText = formatDuration(totalSeconds);

            rows.add(new SessionRow(title, copy, timeText, totalSeconds));
        }

        rows.sort(Comparator.comparingInt((SessionRow r) -> r.totalSeconds).reversed());
        return rows;
    }

    private String formatDuration(int totalSeconds) {
        int safeSeconds = Math.max(0, totalSeconds);
        int hours = safeSeconds / 3600;
        int minutes = (safeSeconds % 3600) / 60;
        return String.format("%02dh %02dm", hours, minutes);
    }

    private void renderRows(List<SessionRow> rows) {
        if (rows.isEmpty()) {
            showRow(1, "No sessions yet", "Start monitoring and reopen dashboard.", "-");
            showRow(2, "-", "-", "-");
            showRow(3, "-", "-", "-");
            return;
        }
        showRowFromList(1, rows, 0);
        showRowFromList(2, rows, 1);
        showRowFromList(3, rows, 2);
    }

    private void showRowFromList(int uiRowIndex, List<SessionRow> rows, int dataIndex) {
        if (dataIndex >= rows.size()) {
            showRow(uiRowIndex, "-", "-", "-");
            return;
        }
        SessionRow row = rows.get(dataIndex);
        showRow(uiRowIndex, row.title, row.copy, row.time);
    }

    private void showRow(int row, String title, String copy, String time) {
        if (row == 1) {
            activityTitle1.setText(title);
            activityCopy1.setText(copy);
            activityTime1.setText(time);
        } else if (row == 2) {
            activityTitle2.setText(title);
            activityCopy2.setText(copy);
            activityTime2.setText(time);
        } else {
            activityTitle3.setText(title);
            activityCopy3.setText(copy);
            activityTime3.setText(time);
        }
    }

    private static class SessionRow {
        private final String title;
        private final String copy;
        private final String time;
        private final int totalSeconds;

        private SessionRow(String title, String copy, String time, int totalSeconds) {
            this.title = title;
            this.copy = copy;
            this.time = time;
            this.totalSeconds = totalSeconds;
        }
    }

}
