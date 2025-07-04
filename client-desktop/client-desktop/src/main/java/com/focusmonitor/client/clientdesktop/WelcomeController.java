package com.focusmonitor.client.clientdesktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;

public class WelcomeController {
    @FXML
    private ImageView logoImage;

    public void initialize() {
        InputStream stream = getClass().getResourceAsStream("image/img.png");
        System.out.println("Stream is null? " + (stream == null));
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

    public void submitSignIn(ActionEvent actionEvent) {
    }

    public void backToMainPanel(ActionEvent actionEvent) {

        rightBottomPanel.visibleProperty().setValue(true);
        signInForm.setVisible(false);
        signUpForm.setVisible(false);

    }

    public void submitSignUp(ActionEvent actionEvent) {
    }
}
