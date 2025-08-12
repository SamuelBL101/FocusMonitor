package com.focusmonitor.client.clientdesktop.communication;

import com.focusmonitor.client.clientdesktop.modules.UsageSession;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UsageSender {
    private static final String API_URL = "http://localhost:8080/api/usage";

    public static void sendUsage(UsageSession usageData) {
        try {

            // otvor HTTP spojenie
            HttpClient client = HttpClient.newHttpClient();
            String json = usageData.toJson();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URL(API_URL).toURI())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() != 200 && response.statusCode() != 201) {
                            System.out.println("Chyba pri odosielaní: " + response.statusCode());
                        } else {
                            System.out.println("Údaje úspešne odoslané.");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
