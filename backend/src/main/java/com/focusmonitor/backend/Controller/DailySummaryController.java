package com.focusmonitor.backend.Controller;

import com.focusmonitor.backend.model.DailySummary;
import com.focusmonitor.backend.repository.UserRepository;
import com.focusmonitor.backend.services.DailySummaryService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/dailysummary")
public class DailySummaryController {
    private final DailySummaryService dailySummaryService;
    private final UserRepository userRepository;
    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
    public DailySummaryController(DailySummaryService dailySummaryService, UserRepository userRepository){
        this.dailySummaryService = dailySummaryService;
        this.userRepository = userRepository;
    }
    @GetMapping("/last7days")
    public List<DailySummary> get7daysSummary(){
        return dailySummaryService.getLast7Days(getCurrentUserId());
    }
}
