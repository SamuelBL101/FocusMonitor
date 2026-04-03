package com.focusmonitor.backend.services;

import com.focusmonitor.backend.dto.TodayAppSummaryResponse;
import com.focusmonitor.backend.model.ActivitySession;
import com.focusmonitor.backend.model.AppDefinition;
import com.focusmonitor.backend.model.User;
import com.focusmonitor.backend.repository.ActivitySessionRepository;
import com.focusmonitor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivitySessionService {
    private final ActivitySessionRepository activitySessionRepository;
    private final UserRepository userRepository;

    public ActivitySession startSession(UUID userId, String appName, String windowTitle) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Couldn't get User to start  new session"));
        ActivitySession activitySession = new ActivitySession();
        activitySession.setStartedAt(Instant.now());
        activitySession.setUser(user);
        activitySession.setWindowTitle(windowTitle);
        activitySession.setIdleSeconds(0);

        return activitySessionRepository.save(activitySession);
    }

    public ActivitySession endSession(UUID userId) {
        Optional<ActivitySession> session = activitySessionRepository
                .findFirstByUserIdAndEndedAtIsNullOrderByStartedAtDesc(userId);

        if (session.isEmpty()) {
            return null;
        }

        session.get().setEndedAt(Instant.now());
        return activitySessionRepository.save(session.get());
    }

    public List<ActivitySession> getTodaySessions(UUID userId) {

        Instant start = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = LocalDate.now().atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
        return activitySessionRepository.findByUserIdAndStartedAtBetween(userId, start, end);
    }

    public List<TodayAppSummaryResponse> getTodaySummary(UUID userId) {
        List<ActivitySession> sessions = getTodaySessions(userId);
        Map<String, SummaryAccumulator> byAppName = new HashMap<>();

        for (ActivitySession session : sessions) {
            String appName = resolveAppName(session);
            int duration = resolveDurationSeconds(session);

            SummaryAccumulator accumulator = byAppName.computeIfAbsent(appName, ignored -> new SummaryAccumulator());
            accumulator.totalSeconds += duration;
            accumulator.sessionsCount += 1;
        }

        List<TodayAppSummaryResponse> response = new ArrayList<>();
        for (Map.Entry<String, SummaryAccumulator> entry : byAppName.entrySet()) {
            response.add(new TodayAppSummaryResponse(
                    entry.getKey(),
                    entry.getValue().totalSeconds,
                    entry.getValue().sessionsCount
            ));
        }

        response.sort(Comparator.comparing(TodayAppSummaryResponse::getTotalSeconds).reversed());
        return response;
    }

    private String resolveAppName(ActivitySession session) {
        AppDefinition appDefinition = session.getAppDefinition();
        if (appDefinition != null && appDefinition.getDisplayName() != null && !appDefinition.getDisplayName().isBlank()) {
            return appDefinition.getDisplayName();
        }
        if (session.getWindowTitle() != null && !session.getWindowTitle().isBlank()) {
            return session.getWindowTitle();
        }
        return "Unknown app";
    }

    private int resolveDurationSeconds(ActivitySession session) {
        if (session.getDurationSeconds() != null && session.getDurationSeconds() >= 0) {
            return session.getDurationSeconds();
        }
        if (session.getStartedAt() == null) {
            return 0;
        }
        Instant endedAt = session.getEndedAt() != null ? session.getEndedAt() : Instant.now();
        long diff = endedAt.getEpochSecond() - session.getStartedAt().getEpochSecond();
        return (int) Math.max(0, diff);
    }

    private static class SummaryAccumulator {
        private int totalSeconds;
        private int sessionsCount;
    }
}
