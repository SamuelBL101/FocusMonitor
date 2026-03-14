package com.focusmonitor.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class ActivitySessionResponse {
    private UUID id;
    private String appName;
    private String windowTitle;
    private Instant startedAt;
    private Instant endedAt;
    private Integer durationSeconds;
    private Integer idleSeconds;
}