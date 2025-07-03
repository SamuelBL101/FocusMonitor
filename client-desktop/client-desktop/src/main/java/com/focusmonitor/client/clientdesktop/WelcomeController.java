package com.focusmonitor.client.clientdesktop;

import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class WelcomeController {
    @FXML
    protected void onWelcomeButtonClick() {
        System.out.println("Welcome button clicked!");
    }
    @FXML
    private ImageView logoImage;

    public void initialize() {
        InputStream stream = getClass().getResourceAsStream("image/img.png");
        System.out.println("Stream is null? " + (stream == null));
        Image image = new Image(stream);

        logoImage.setImage(image);

    }
}
