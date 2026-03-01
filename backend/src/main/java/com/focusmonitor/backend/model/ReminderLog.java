package com.focusmonitor.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reminder_logs")
public class ReminderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reminder_id", nullable = false)
    private Reminder reminder;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt;

    @Column(name = "dismissed_at")
    private Instant dismissedAt;

    @Column(name = "snoozed_until")
    private Instant snoozedUntil;


}