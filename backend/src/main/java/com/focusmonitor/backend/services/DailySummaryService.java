package com.focusmonitor.backend.services;

import com.focusmonitor.backend.model.ActivitySession;
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
import java.util.List;
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
        for(ActivitySession activitySession : list){
            if (activitySession.getDurationSeconds() != null) {
                totalSeconds += activitySession.getDurationSeconds();
            }
        }

        DailySummary dailySummary = dailySummaryRepository.findByUser_IdAndDate(userId, date)
                .orElseGet(DailySummary::new);
        dailySummary.setUser(user);
        dailySummary.setComputedAt(Instant.now());
        dailySummary.setDate(date);
        //todo
        dailySummary.setTopApp(null);
        dailySummary.setSessionsCount(list.size());
        //todo
        dailySummary.setProductiveSeconds(totalSeconds);
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
}
