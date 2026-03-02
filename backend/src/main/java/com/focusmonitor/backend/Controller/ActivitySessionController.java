package com.focusmonitor.backend.Controller;

import com.focusmonitor.backend.dto.StartSessionRequest;
import com.focusmonitor.backend.model.ActivitySession;
import com.focusmonitor.backend.repository.UserRepository;
import com.focusmonitor.backend.services.ActivitySessionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activitysession")

public class ActivitySessionController {
    private final ActivitySessionService activitySessionService;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    public ActivitySessionController(ActivitySessionService activitySessionService, UserRepository userRepository) {
        this.activitySessionService = activitySessionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/start")
    public ActivitySession startsession(@RequestBody StartSessionRequest request) {
        return this.activitySessionService.startSession(getCurrentUserId(), request.getAppName(), request.getWindowTitle());

    }

    @PutMapping("/end")
    public ActivitySession endSession() {
        return this.activitySessionService.endSession(getCurrentUserId());
    }

    @GetMapping("/todaylist")
    public List<ActivitySession> getTodaySessionList() {
        return this.activitySessionService.getTodaySessions(getCurrentUserId());
    }
}
