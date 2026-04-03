package com.focusmonitor.client.clientdesktop.controller;

import com.focusmonitor.client.clientdesktop.AppConfig;
import com.focusmonitor.client.clientdesktop.communication.UsageSender;
import com.focusmonitor.client.clientdesktop.modules.ActivityTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private Button monitorToggleButton;
    @FXML
    private Label monitorStateLabel;
    @FXML
    private Label stateValueLabel;
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
    @FXML
    private Label dailySummaryLabel;
    @FXML
    private Label todayTotalLabel;
    @FXML
    private Label todayProductiveLabel;
    @FXML
    private Label todayRatioLabel;
    @FXML
    private VBox dailyFocusBarsBox;

    public void initialize() {
        welcomeLabel.setText("Welcome to your Dashboard!");
        updateMonitoringUi(false);
        loadTodaySessions();
        loadDailyFocus();
    }

    public void logout() {
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        stopMonitoringAndFinalizeSession();
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

    @FXML
    public void toggleMonitor(ActionEvent actionEvent) {
        if (isMonitoring()) {
            stopMonitoringAndFinalizeSession();
            loadTodaySessions();
            loadDailyFocus();
            return;
        }

        if (activityTracker == null) {
            this.activityTracker = new ActivityTracker(this);
        }
        activityTrackerThread = new Thread(activityTracker);
        activityTrackerThread.setDaemon(true);
        activityTrackerThread.start();
        updateMonitoringUi(true);
    }

    public void updateActivity(String currentActivity) {
        javafx.application.Platform.runLater(() -> activeWindowLabel.setText(currentActivity));
    }

    @FXML
    public void refreshTodaySummary(ActionEvent actionEvent) {
        setRow(1, "Refreshing...", "Loading latest activity from server.", "-");
        setRowVisible(2, false);
        setRowVisible(3, false);
        loadTodaySessions();
        loadDailyFocus();
    }

    private void loadTodaySessions() {
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        String token = prefs.get("jwtToken", null);
        if (token == null || token.isBlank()) {
            showEmptyState("No session data", "Please sign in again.");
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
                        javafx.application.Platform.runLater(() ->
                                showEmptyState("Load failed", "Server returned HTTP " + response.statusCode() + ".")
                        );
                        return;
                    }

                    List<SessionRow> rows = parseRows(response.body());
                    javafx.application.Platform.runLater(() -> renderRows(rows));
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() ->
                            showEmptyState("Connection error", ex.getClass().getSimpleName())
                    );
                    return null;
                });
    }

    private List<SessionRow> parseRows(String body) {
        JSONArray array = new JSONArray(body);
        List<SessionRow> rows = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            String title = item.optString("appName", "").trim();
            if (title.isEmpty()) {
                title = "Unknown app";
            }

            int durationSeconds = item.optInt("totalSeconds", 0);
            int sessionsCount = item.optInt("sessionsCount", 0);
            String copy = "Sessions: " + sessionsCount;
            String timeText = formatDuration(durationSeconds);

            rows.add(new SessionRow(title, copy, timeText, durationSeconds));
        }

        rows.sort(Comparator.comparingInt((SessionRow r) -> r.durationSeconds).reversed());
        return rows;
    }

    private void loadDailyFocus() {
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        String token = prefs.get("jwtToken", null);
        if (token == null || token.isBlank()) {
            renderDailyFocusEmpty("Sign in again to load your focus stats.");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getBaseURL() + "/api/dailysummary/last7days"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200) {
                        javafx.application.Platform.runLater(() ->
                                renderDailyFocusEmpty("Could not load focus stats (HTTP " + response.statusCode() + ").")
                        );
                        return;
                    }

                    List<DailyFocusRow> rows = parseDailyFocusRows(response.body());
                    javafx.application.Platform.runLater(() -> renderDailyFocus(rows));
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() ->
                            renderDailyFocusEmpty("Connection issue while loading Daily Focus.")
                    );
                    return null;
                });
    }

    private List<DailyFocusRow> parseDailyFocusRows(String body) {
        JSONArray array = new JSONArray(body);
        List<DailyFocusRow> rows = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            String date = item.optString("date", "");
            int total = item.optInt("totalSeconds", 0);
            int productive = item.optInt("productiveSeconds", 0);
            rows.add(new DailyFocusRow(date, total, productive));
        }

        rows.sort(Comparator.comparing(row -> row.date));
        return rows;
    }

    private void renderDailyFocus(List<DailyFocusRow> rows) {
        if (rows.isEmpty()) {
            renderDailyFocusEmpty("No summary data yet. Track activity to populate this chart.");
            return;
        }

        DailyFocusRow latest = rows.get(rows.size() - 1);
        int todayTotal = Math.max(0, latest.totalSeconds);
        int todayProductive = Math.max(0, latest.productiveSeconds);
        int ratio = todayTotal == 0 ? 0 : (int) Math.round((todayProductive * 100.0) / todayTotal);

        todayTotalLabel.setText(formatDuration(todayTotal));
        todayProductiveLabel.setText(formatDuration(todayProductive));
        todayRatioLabel.setText(ratio + "%");

        int last7Total = rows.stream().mapToInt(row -> Math.max(0, row.totalSeconds)).sum();
        dailySummaryLabel.setText("Last 7 days total: " + formatDuration(last7Total) + ". Productive ratio by day:");

        int maxTotal = rows.stream()
                .mapToInt(row -> Math.max(1, row.totalSeconds))
                .max()
                .orElse(1);

        dailyFocusBarsBox.getChildren().clear();
        for (DailyFocusRow row : rows) {
            dailyFocusBarsBox.getChildren().add(buildDailyFocusBarRow(row, maxTotal));
        }
    }

    private HBox buildDailyFocusBarRow(DailyFocusRow row, int maxTotal) {
        Label dayLabel = new Label(shortDate(row.date));
        dayLabel.getStyleClass().add("focus-day-label");
        dayLabel.setMinWidth(42);

        int total = Math.max(0, row.totalSeconds);
        int productive = Math.max(0, row.productiveSeconds);
        int ratio = total == 0 ? 0 : (int) Math.round((productive * 100.0) / total);

        ProgressBar totalBar = new ProgressBar(total / (double) maxTotal);
        totalBar.getStyleClass().add("focus-total-bar");
        totalBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(totalBar, Priority.ALWAYS);

        Label ratioLabel = new Label(ratio + "%");
        ratioLabel.getStyleClass().add("focus-day-ratio");
        ratioLabel.setMinWidth(38);

        HBox rowBox = new HBox(8, dayLabel, totalBar, ratioLabel);
        rowBox.getStyleClass().add("focus-day-row");
        return rowBox;
    }

    private void renderDailyFocusEmpty(String message) {
        todayTotalLabel.setText("00h 00m");
        todayProductiveLabel.setText("00h 00m");
        todayRatioLabel.setText("0%");
        dailySummaryLabel.setText(message);
        dailyFocusBarsBox.getChildren().clear();
    }

    private String formatDuration(int totalSeconds) {
        int safeSeconds = Math.max(0, totalSeconds);
        int hours = safeSeconds / 3600;
        int minutes = (safeSeconds % 3600) / 60;
        int seconds = safeSeconds % 60;
        if (hours > 0) {
            return String.format("%02dh %02dm", hours, minutes);
        }
        return String.format("%02dm %02ds", minutes, seconds);
    }

    private String shortDate(String isoDate) {
        try {
            LocalDate date = LocalDate.parse(isoDate);
            return date.format(DateTimeFormatter.ofPattern("dd.MM"));
        } catch (Exception ignored) {
            return "--.--";
        }
    }

    private void renderRows(List<SessionRow> rows) {
        if (rows.isEmpty()) {
            showEmptyState("No sessions yet", "Start monitoring to collect today's activity.");
            return;
        }

        SessionRow row1 = rows.get(0);
        setRow(1, row1.title, row1.copy, row1.time);
        setRowVisible(1, true);

        if (rows.size() > 1) {
            SessionRow row2 = rows.get(1);
            setRow(2, row2.title, row2.copy, row2.time);
            setRowVisible(2, true);
        } else {
            setRowVisible(2, false);
        }

        if (rows.size() > 2) {
            SessionRow row3 = rows.get(2);
            setRow(3, row3.title, row3.copy, row3.time);
            setRowVisible(3, true);
        } else {
            setRowVisible(3, false);
        }
    }

    private void showEmptyState(String title, String copy) {
        setRow(1, title, copy, "-");
        setRowVisible(1, true);
        setRowVisible(2, false);
        setRowVisible(3, false);
    }

    private void setRow(int rowIndex, String title, String copy, String time) {
        switch (rowIndex) {
            case 1 -> {
                activityTitle1.setText(title);
                activityCopy1.setText(copy);
                activityTime1.setText(time);
            }
            case 2 -> {
                activityTitle2.setText(title);
                activityCopy2.setText(copy);
                activityTime2.setText(time);
            }
            case 3 -> {
                activityTitle3.setText(title);
                activityCopy3.setText(copy);
                activityTime3.setText(time);
            }
            default -> {
            }
        }
    }

    private void setRowVisible(int rowIndex, boolean visible) {
        switch (rowIndex) {
            case 1 -> {
                activityTitle1.getParent().getParent().setVisible(visible);
                activityTitle1.getParent().getParent().setManaged(visible);
            }
            case 2 -> {
                activityTitle2.getParent().getParent().setVisible(visible);
                activityTitle2.getParent().getParent().setManaged(visible);
            }
            case 3 -> {
                activityTitle3.getParent().getParent().setVisible(visible);
                activityTitle3.getParent().getParent().setManaged(visible);
            }
            default -> {
            }
        }
    }

    private boolean isMonitoring() {
        return activityTracker != null && activityTracker.isRunning();
    }

    private void stopMonitoringAndFinalizeSession() {
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
        updateMonitoringUi(false);
        activityTrackerThread = null;
    }

    private void updateMonitoringUi(boolean running) {
        if (monitorToggleButton == null || monitorStateLabel == null || stateValueLabel == null) {
            return;
        }

        if (running) {
            monitorToggleButton.setText("Stop Monitoring");
            monitorStateLabel.setText("Monitoring is running.");
            stateValueLabel.setText("Running");
        } else {
            monitorToggleButton.setText("Start Monitoring");
            monitorStateLabel.setText("Monitoring is idle.");
            stateValueLabel.setText("Idle");
        }
    }

    private static class SessionRow {
        private final String title;
        private final String copy;
        private final String time;
        private final int durationSeconds;

        private SessionRow(String title, String copy, String time, int durationSeconds) {
            this.title = title;
            this.copy = copy;
            this.time = time;
            this.durationSeconds = durationSeconds;
        }
    }

    private static class DailyFocusRow {
        private final String date;
        private final int totalSeconds;
        private final int productiveSeconds;

        private DailyFocusRow(String date, int totalSeconds, int productiveSeconds) {
            this.date = date;
            this.totalSeconds = totalSeconds;
            this.productiveSeconds = productiveSeconds;
        }
    }
}
