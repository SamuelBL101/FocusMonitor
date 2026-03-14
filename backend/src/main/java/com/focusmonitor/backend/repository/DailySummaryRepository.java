package com.focusmonitor.backend.repository;

import com.focusmonitor.backend.model.DailySummary;
import com.focusmonitor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, UUID> {
    Optional<DailySummary> findByUser_IdAndDate(UUID userId, LocalDate date);
    List<DailySummary> findByUser_IdAndDateBetween(UUID userId, LocalDate start, LocalDate end);
}