package com.focusmonitor.client.clientdesktop.controller;

import com.focusmonitor.client.clientdesktop.AppConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.prefs.Preferences;

public class WelcomeController {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(6);
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    @FXML
    private ImageView logoImage;

    @FXML
    private ImageView logoImageSignUp;

    @FXML
    private VBox loginCard;
    @FXML
    private VBox signUpCard;

    @FXML
    private TextField signInEmail;
    @FXML
    private PasswordField signInPassword;
    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField signUpUsername;
    @FXML
    private TextField signUpEmail;
    @FXML
    private PasswordField signUpPassword;
    @FXML
    private PasswordField signUpConfirmPassword;
    @FXML
    private Label signUpMessageLabel;

    public void initialize() {
        InputStream stream1 = getClass().getResourceAsStream("/com/focusmonitor/client/clientdesktop/image/img.png");
        if (stream1 != null && logoImage != null) {
            logoImage.setImage(new Image(stream1));
        }
        InputStream stream2 = getClass().getResourceAsStream("/com/focusmonitor/client/clientdesktop/image/img.png");
        if (stream2 != null && logoImageSignUp != null) {
            logoImageSignUp.setImage(new Image(stream2));
        }
        loginCard.setVisible(true);
        signUpCard.setVisible(false);

        log("UI initialized. Current API base URL: " + AppConfig.getBaseURL());
    }

    @FXML
    public void switchToSignUp() {
        loginCard.setVisible(false);
        signUpCard.setVisible(true);
        signUpMessageLabel.setText("");
    }

    @FXML
    public void switchToSignIn() {
        signUpCard.setVisible(false);
        loginCard.setVisible(true);
        loginMessageLabel.setText("");
    }

    @FXML
    public void submitSignIn() {
        String email = signInEmail.getText();
        String password = signInPassword.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            loginMessageLabel.setText("Please fill in all fields.");
            return;
        }

        String baseUrl = AppConfig.getBaseURL();
        log("LOGIN started for email=" + email + ", apiBaseUrl=" + baseUrl);
        loginMessageLabel.setText("Signing in...");

        String jsonBody = String.format("""
        {
            "email": "%s",
            "password": "%s"
        }
        """, email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    log("LOGIN response status=" + response.statusCode());
                    if (response.statusCode() == 200) {
                        JSONObject json = new JSONObject(response.body());
                        String token = json.getString("token");
                        String userID = json.getString("userId");

                        Preferences prefs = Preferences.userNodeForPackage(getClass());
                        prefs.put("jwtToken", token);
                        prefs.put("userID", userID);
                        log("LOGIN success. userId=" + userID);
                        Platform.runLater(this::goToMainScreen);
                    } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                        Platform.runLater(() -> loginMessageLabel.setText("Wrong email or password."));
                    } else {
                        Platform.runLater(() -> loginMessageLabel.setText("Login failed (HTTP " + response.statusCode() + ")."));
                    }
                })
                .exceptionally(e -> {
                    String msg = mapNetworkError(e);
                    log("LOGIN failed: " + msg);
                    Platform.runLater(() -> loginMessageLabel.setText(msg));
                    return null;
                });
    }

    @FXML
    public void submitSignUp() {
        String username = signUpUsername.getText();
        String email = signUpEmail.getText();
        String password = signUpPassword.getText();
        String confirmPassword = signUpConfirmPassword.getText();

        if (username == null || username.isBlank() || email == null || email.isBlank()
                || password == null || password.isBlank() || confirmPassword == null || confirmPassword.isBlank()) {
            signUpMessageLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            signUpMessageLabel.setText("Passwords do not match.");
            return;
        }

        String baseUrl = AppConfig.getBaseURL();
        log("REGISTER started for email=" + email + ", apiBaseUrl=" + baseUrl);

        String jsonBody = String.format("""
        {
            "username": "%s",
            "password": "%s",
            "email": "%s"
        }
        """, username, password, email);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/register"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    log("REGISTER response status=" + response.statusCode());
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            signUpMessageLabel.setText("Registration successful. You can sign in now.");
                            switchToSignIn();
                        });
                    } else {
                        Platform.runLater(() -> signUpMessageLabel.setText("Registration failed: " + response.body()));
                    }
                })
                .exceptionally(e -> {
                    String msg = mapNetworkError(e);
                    log("REGISTER failed: " + msg);
                    Platform.runLater(() -> signUpMessageLabel.setText(msg));
                    return null;
                });
    }

    private String mapNetworkError(Throwable throwable) {
        Throwable root = unwrap(throwable);
        if (root instanceof ConnectException) {
            return "Cannot connect to backend. Check if backend is running and URL is correct.";
        }
        if (root instanceof HttpTimeoutException) {
            return "Request timed out. Backend may be down or slow.";
        }
        return "Network error: " + root.getClass().getSimpleName();
    }

    private Throwable unwrap(Throwable throwable) {
        if (throwable instanceof CompletionException && throwable.getCause() != null) {
            return throwable.getCause();
        }
        return throwable;
    }

    private void goToMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/focusmonitor/client/clientdesktop/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) signInEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Focus Monitor - Dashboard");
        } catch (IOException e) {
            log("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void log(String message) {
        System.out.println("[FocusMonitorClient] " + message);
    }
}
