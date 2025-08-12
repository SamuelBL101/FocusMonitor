package com.focusmonitor.client.clientdesktop;

import org.json.JSONObject;

import java.util.Base64;

public class Auth {
    public String getUserIDfromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid token format");
            }
            String payload = parts[1];
            byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);
            String payloadJson = new String(decodedPayload);
            JSONObject jsonObject = new JSONObject(payloadJson);

            return payload;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if an error occurs
        }
    }
}
