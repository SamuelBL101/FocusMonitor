package com.focusmonitor.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String userId;
    private String email;

    public AuthResponse(String token, String userId, String email) {
        this.token = token;
        this.userId = userId;
        this.email = email;
    }
}
