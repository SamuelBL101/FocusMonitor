package com.focusmonitor.backend.services;

import com.focusmonitor.backend.model.ActivitySession;
import com.focusmonitor.backend.repository.ActivitySessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivitySessionServiceTest {

    @Mock
    private ActivitySessionRepository activitySessionRepository;

    @InjectMocks
    private ActivitySessionService activitySessionService;

    @Test
    void endSession_returnsNull_whenNoOpenSession() {
        UUID userId = UUID.randomUUID();

        when(activitySessionRepository
                .findFirstByUserIdAndEndedAtIsNullOrderByStartedAtDesc(userId))
                .thenReturn(Optional.empty());

        activitySessionService.endSession(userId);
        ActivitySession result = activitySessionService.endSession(userId);

        assertNull(result);

    }

    @Test
    void endSession_savesAndReturns_whenSessionExists() {
        UUID userId = UUID.randomUUID();
        ActivitySession fakeSession = new ActivitySession();

        when(activitySessionRepository
                .findFirstByUserIdAndEndedAtIsNullOrderByStartedAtDesc(userId))
                .thenReturn(Optional.of(fakeSession));
        when(activitySessionRepository
                .save(fakeSession))
                .thenReturn(new ActivitySession() );

        activitySessionService.endSession(userId);

        ActivitySession result = activitySessionService.endSession(userId);
        assertNotNull(result);
    }
}