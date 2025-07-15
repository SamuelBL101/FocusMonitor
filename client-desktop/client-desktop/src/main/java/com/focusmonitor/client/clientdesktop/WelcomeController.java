package com.focusmonitor.client.clientdesktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.prefs.Preferences;

public class WelcomeController {
    @FXML
    private ImageView logoImage;

    public void initialize() {
        InputStream stream = getClass().getResourceAsStream("image/img.png");
        Image image = new Image(stream);

        logoImage.setImage(image);

    }
    @FXML
    private VBox rightBottomPanel;
    @FXML
    private VBox signInForm;
    public void handleSignIn(ActionEvent actionEvent) {
        rightBottomPanel.visibleProperty().setValue(false);
        signInForm.setVisible(true);
    }

    //Register

    @FXML
    private VBox signUpForm;
    public void handleSignUp(ActionEvent actionEvent) {
        rightBottomPanel.visibleProperty().setValue(false);
        signUpForm.setVisible(true);
    }
    @FXML private TextField signInUsername;
    @FXML private PasswordField signInPassword;
    @FXML private Label loginMessageLabel;

    public void submitSignIn(ActionEvent actionEvent) {
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
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    // Spracuj JSON odpoveď a ulož token
                    JSONObject json = new JSONObject(responseBody);
                    String token = json.getString("token");
                    System.out.println("JWT token: " + token);

                    Preferences prefs = Preferences.userNodeForPackage(getClass());
                    prefs.put("jwtToken", token);
                    String savedToken = prefs.get("jwtToken", null);
                    System.out.println("Token loaded: " + savedToken);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public void backToMainPanel(ActionEvent actionEvent) {

        rightBottomPanel.visibleProperty().setValue(true);
        signInForm.setVisible(false);
        signUpForm.setVisible(false);

    }
    @FXML private TextField signUpUsername;
    @FXML private TextField signUpEmail;
    @FXML private PasswordField signUpPassword;
    @FXML private PasswordField signUpConfrmPassword;
    public void submitSignUp(ActionEvent actionEvent) {
        String username = signUpUsername.getText();
        String email = signUpEmail.getText();
        String password = signUpPassword.getText();
        String confirmPassword = signUpConfrmPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Vyplň všetky polia.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Heslá sa nezhodujú.");
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
                    System.out.println("Stavový kód: " + response.statusCode());
                    System.out.println("Odpoveď servera: " + response.body());

                    // UI logika po úspechu
                    if (response.statusCode() == 200) {
                        javafx.application.Platform.runLater(() -> {
                            //showPanel(rightBottomPanel);
                            showMessage("Registrácia úspešná.");
                        });
                    } else {
                        javafx.application.Platform.runLater(() -> showMessage("Chyba: " + response.body()));
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> showMessage("Chyba spojenia s API."));
                    return null;
                });

    }

    private void showMessage(String s) {
    }
}
