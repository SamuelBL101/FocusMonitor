package com.focusmonitor.client.clientdesktop;

import com.focusmonitor.client.clientdesktop.communication.UsageSender;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/focusmonitor/client/clientdesktop/welcomepage.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("FocusMonitor");
        stage.setScene(new Scene(root, 1280, 720));
        stage.getScene().getStylesheets().add(getClass().getResource("/com/focusmonitor/client/clientdesktop/style.css").toExternalForm());
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            UsageSender.endSession();
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        });

    }

    public static void main(String[] args) {
        launch();
    }
}