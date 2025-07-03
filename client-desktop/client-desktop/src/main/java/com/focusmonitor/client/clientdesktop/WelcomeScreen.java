package com.focusmonitor.client.clientdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeScreen extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WelcomeScreen.class.getResource("welcomepage.fxml"));

        //Parent root = FXMLLoader.load(getClass().getResource("/welcomepage.fxml"));
        Parent root = fxmlLoader.load();
    stage.setTitle("Welcome");
    stage.setScene(new Scene(root));
    stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}