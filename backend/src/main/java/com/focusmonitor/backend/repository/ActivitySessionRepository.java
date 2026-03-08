package com.focusmonitor.backend.repository;

import com.focusmonitor.backend.model.ActivitySession;
import com.focusmonitor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivitySessionRepository extends JpaRepository<ActivitySession, UUID> {
    Optional<ActivitySession> findFirstByUserIdAndEndedAtIsNullOrderByStartedAtDesc(UUID userId);

    List<ActivitySession> findByUserIdAndStartedAtBetween(UUID userId, Instant start, Instant end);


}
