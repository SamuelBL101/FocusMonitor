package com.focusmonitor.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayAppSummaryResponse {
    private String appName;
    private Integer totalSeconds;
    private Integer sessionsCount;
}
