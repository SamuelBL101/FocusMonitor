module com.focusmonitor.client.clientdesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires java.net.http;
    requires org.json;
    requires java.prefs;
    requires com.sun.jna.platform;
    requires com.sun.jna;

    opens com.focusmonitor.client.clientdesktop to javafx.fxml;
    exports com.focusmonitor.client.clientdesktop;
    exports com.focusmonitor.client.clientdesktop.modules;
    opens com.focusmonitor.client.clientdesktop.modules to javafx.fxml;
    exports com.focusmonitor.client.clientdesktop.controller;
    opens com.focusmonitor.client.clientdesktop.controller to javafx.fxml;
}