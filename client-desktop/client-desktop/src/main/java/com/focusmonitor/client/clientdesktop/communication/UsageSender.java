package com.focusmonitor.client.clientdesktop.communication;

import com.focusmonitor.client.clientdesktop.modules.UsageSession;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class UsageSender {
    private static final String API_URL = "http://localhost:8080/api/usage";
    private static LocalDateTime timeSent = null;
    public static void sendUsage(UsageSession usageData) {
        try {
            if(timeSent != null) {
               long secondsSinceLastSend = java.time.Duration.between(timeSent, LocalDateTime.now()).getSeconds();
               if(secondsSinceLastSend < 60) {
                   return; // Ak uplynulo menej ako 60 sekúnd, neodosielaj
               }
            }
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
                            timeSent = LocalDateTime.now();

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
