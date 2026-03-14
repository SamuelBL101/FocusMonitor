package com.focusmonitor.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DailySummaryResponse {
    private UUID userId;
    private LocalDate date;
    private Integer totalSeconds;
    private Integer productiveSeconds;
    private String topApp;
    private Integer sessionsCount;
    private Instant computedAt;
}
