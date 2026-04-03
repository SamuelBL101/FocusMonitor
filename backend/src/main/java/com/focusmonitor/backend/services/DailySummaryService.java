package com.focusmonitor.backend.services;

import com.focusmonitor.backend.model.ActivitySession;
import com.focusmonitor.backend.model.AppDefinition;
import com.focusmonitor.backend.model.DailySummary;
import com.focusmonitor.backend.model.User;
import com.focusmonitor.backend.repository.ActivitySessionRepository;
import com.focusmonitor.backend.repository.DailySummaryRepository;
import com.focusmonitor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailySummaryService {
    private final DailySummaryRepository dailySummaryRepository;
    private final UserRepository userRepository;
    private final ActivitySessionRepository activitySessionRepository;

    public DailySummary calculateDailySummary(UUID userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow();
        ZoneId zoneId = resolveZoneId(user);
        Instant start = date.atStartOfDay(zoneId).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(zoneId).toInstant().minusSeconds(1);
        List<ActivitySession> list = activitySessionRepository.findByUserIdAndStartedAtBetween(userId, start, end);

        int totalSeconds = 0;
        int productiveSeconds = 0;
        Map<UUID, AppDurationAggregate> appDurations = new HashMap<>();

        for (ActivitySession activitySession : list) {
            int sessionDuration = resolveSessionDurationSeconds(activitySession);
            totalSeconds += sessionDuration;

            AppDefinition appDefinition = activitySession.getAppDefinition();
            if (appDefinition == null) {
                continue;
            }

            if (isProductive(appDefinition)) {
                productiveSeconds += sessionDuration;
            }

            UUID appId = appDefinition.getId();
            if (appId == null) {
                continue;
            }

            AppDurationAggregate aggregate = appDurations.computeIfAbsent(appId, ignored -> new AppDurationAggregate(appDefinition));
            aggregate.totalSeconds += sessionDuration;
        }

        AppDefinition topApp = null;
        int topAppSeconds = -1;
        for (AppDurationAggregate aggregate : appDurations.values()) {
            if (aggregate.totalSeconds > topAppSeconds) {
                topApp = aggregate.appDefinition;
                topAppSeconds = aggregate.totalSeconds;
            }
        }

        DailySummary dailySummary = dailySummaryRepository.findByUser_IdAndDate(userId, date)
                .orElseGet(DailySummary::new);
        dailySummary.setUser(user);
        dailySummary.setComputedAt(Instant.now());
        dailySummary.setDate(date);
        dailySummary.setTopApp(topApp);
        dailySummary.setSessionsCount(list.size());
        dailySummary.setProductiveSeconds(productiveSeconds);
        dailySummary.setTotalSeconds(totalSeconds);
        return  dailySummaryRepository.save(dailySummary);
    }
    public List<DailySummary> getLast7Days(UUID userId){
        User user = userRepository.findById(userId).orElseThrow();
        LocalDate end = LocalDate.now(resolveZoneId(user));
        LocalDate start = end.minusDays(6);
        return dailySummaryRepository.findByUser_IdAndDateBetween(userId, start, end);
    }

    private ZoneId resolveZoneId(User user) {
        try {
            return ZoneId.of(user.getTimezone());
        } catch (Exception ignored) {
            return ZoneOffset.UTC;
        }
    }

    private int resolveSessionDurationSeconds(ActivitySession session) {
        if (session.getDurationSeconds() != null && session.getDurationSeconds() >= 0) {
            return session.getDurationSeconds();
        }
        Instant startedAt = session.getStartedAt();
        if (startedAt == null) {
            return 0;
        }
        Instant endedAt = session.getEndedAt() != null ? session.getEndedAt() : Instant.now();
        long seconds = endedAt.getEpochSecond() - startedAt.getEpochSecond();
        return (int) Math.max(0, seconds);
    }

    private boolean isProductive(AppDefinition appDefinition) {
        if (appDefinition.getCategory() == null) {
            return false;
        }
        Boolean productive = appDefinition.getCategory().getIsProductive();
        return productive != null && productive;
    }

    private static class AppDurationAggregate {
        private final AppDefinition appDefinition;
        private int totalSeconds;

        private AppDurationAggregate(AppDefinition appDefinition) {
            this.appDefinition = appDefinition;
        }
    }
}
