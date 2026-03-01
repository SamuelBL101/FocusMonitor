package com.focusmonitor.backend.repository;

import com.focusmonitor.backend.model.Usage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UsageRepository extends JpaRepository<Usage, Long> {

    List<Usage> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
