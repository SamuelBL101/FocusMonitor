package com.focusmonitor.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class AuthResponse {
    String token;
    public AuthResponse(String token){
       this.token =token;
    }
}
