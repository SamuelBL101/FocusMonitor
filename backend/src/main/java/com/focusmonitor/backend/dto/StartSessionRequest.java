package com.focusmonitor.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StartSessionRequest {
    private String appName;
    private String windowTitle;
}