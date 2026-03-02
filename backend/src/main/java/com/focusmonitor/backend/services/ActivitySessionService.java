package com.focusmonitor.backend.services;

import com.focusmonitor.backend.model.ActivitySession;
import com.focusmonitor.backend.model.User;
import com.focusmonitor.backend.repository.ActivitySessionRepository;
import com.focusmonitor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
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
        ActivitySession activitySession = activitySessionRepository.findByUserIdAndEndedAtIsNull(userId).orElseThrow(() -> new RuntimeException("Couldn't return AcitivitySesion on END sesion"));
        activitySession.setEndedAt(Instant.now());
        return activitySessionRepository.save(activitySession);
    }

    public List<ActivitySession> getTodaySessions(UUID userId) {

        Instant start = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = LocalDate.now().atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
        return activitySessionRepository.findByUserIdAndStartedAtBetween(userId, start, end);
    }
}
