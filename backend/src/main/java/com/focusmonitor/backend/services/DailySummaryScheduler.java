package com.focusmonitor.backend.services;

import com.focusmonitor.backend.model.User;
import com.focusmonitor.backend.repository.DailySummaryRepository;
import com.focusmonitor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DailySummaryScheduler {
    private final UserRepository userRepository;
    private final DailySummaryService dailySummaryService;
    @Scheduled(cron = "0 0 0 * * *")
    public void calculateYesterdaySummary(){
        List<User> userList = userRepository.findAll();

        for(User user: userList){
            dailySummaryService.calculateDailySummary(user.getId(), LocalDate.now().minusDays(1));
        }
    }
}
