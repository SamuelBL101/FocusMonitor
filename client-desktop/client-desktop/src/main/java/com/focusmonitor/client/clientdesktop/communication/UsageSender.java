package com.focusmonitor.client.clientdesktop.communication;

import com.focusmonitor.client.clientdesktop.AppConfig;
import com.focusmonitor.client.clientdesktop.controller.WelcomeController;
import com.focusmonitor.client.clientdesktop.modules.UsageSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.prefs.Preferences;

public class UsageSender {
    private static final String API_URL = "http://localhost:8080/api/usage";
    private static LocalDateTime timeSent = null;
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void sendUsage(UsageSession usageData) {

    }
    public static void callStartSession(String appName, String windowTitle){
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        String token = prefs.get("jwtToken",null);
        String jsonBody = String.format("""
                {
                    "appName" : "%s",
                    "windowTitle" : "%s"
                }
                """, appName, windowTitle);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getBaseURL() + "/api/activitysession/start"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("Session stareted");
                });


    }


    public static void endSession() {
        Preferences prefs = Preferences.userNodeForPackage(WelcomeController.class);
        String token = prefs.get("jwtToken", null);
        //System.out.println("Token: " + token);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getBaseURL() +"/api/activitysession/end"))
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
