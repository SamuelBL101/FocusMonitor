package com.focusmonitor.client.clientdesktop;

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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.prefs.Preferences;

public class WelcomeController {

    @FXML
    private ImageView logoImage;

    @FXML
    private ImageView logoImageSignUp;

    @FXML
    private VBox loginCard;
    @FXML
    private VBox signUpCard;

    // Polia z FXML, každý fx:id iba raz
    @FXML
    private TextField signInUsername;
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
        // Set up both logo images
        InputStream stream1 = getClass().getResourceAsStream("image/img.png");
        if (stream1 != null && logoImage != null) {
            logoImage.setImage(new Image(stream1));
        }
        InputStream stream2 = getClass().getResourceAsStream("image/img.png");
        if (stream2 != null && logoImageSignUp != null) {
            logoImageSignUp.setImage(new Image(stream2));
        }
        // Initial visibility
        loginCard.setVisible(true);
        signUpCard.setVisible(false);
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
        String username = signInUsername.getText();
        String password = signInPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Vyplň všetky polia.");
            return;
        }

        String jsonBody = String.format("""
        {
            "username": "%s",
            "password": "%s"
        }
        """, username, password);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        JSONObject json = new JSONObject(response.body());
                        String token = json.getString("token");

                        Preferences prefs = Preferences.userNodeForPackage(getClass());
                        prefs.put("jwtToken", token);

                        Platform.runLater(this::goToMainScreen);
                    } else {
                        Platform.runLater(() -> loginMessageLabel.setText("Nesprávne používateľské meno alebo heslo."));
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> loginMessageLabel.setText("Chyba spojenia so serverom."));
                    return null;
                });
    }

    @FXML
    public void submitSignUp() {
        String username = signUpUsername.getText();
        String email = signUpEmail.getText();
        String password = signUpPassword.getText();
        String confirmPassword = signUpConfirmPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            signUpMessageLabel.setText("Vyplň všetky polia.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            signUpMessageLabel.setText("Heslá sa nezhodujú.");
            return;
        }

        String jsonBody = String.format("""
        {
            "username": "%s",
            "password": "%s",
            "email": "%s"
        }
        """, username, password, email);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            signUpMessageLabel.setText("Registrácia úspešná! Môžete sa prihlásiť.");
                            switchToSignIn();
                        });
                    } else {
                        Platform.runLater(() -> signUpMessageLabel.setText("Chyba: " + response.body()));
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> signUpMessageLabel.setText("Chyba spojenia so serverom."));
                    return null;
                });
    }

    private void goToMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage.fxml")); // uprav podľa cesty
            Parent root = loader.load();
            Stage stage = (Stage) signInUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Focus Monitor - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
