package com.focusmonitor.backend.services;
import com.focusmonitor.backend.model.Usage;
import com.focusmonitor.backend.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class UsageService {

    @Autowired
    private UsageRepository usageRepository;

    public Usage saveUsage(Usage usage) {
        return usageRepository.save(usage);
    }

    public List<Usage> getUsageByUserAndDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return usageRepository.findByUserIdAndStartTimeBetween(userId, startOfDay, endOfDay);
    }
}

