package com.focusmonitor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "activity_sessions", indexes = {
        @Index(name = "idx_activity_user_app",
                columnList = "user_id, app_definition_id"),
        @Index(name = "idx_activity_user_time",
                columnList = "user_id, started_at"),
        @Index(name = "idx_activity_active_sessions",
                columnList = "user_id")})
public class ActivitySession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "app_definition_id")

    private AppDefinition appDefinition;

    @Size(max = 500)
    @Column(name = "window_title", length = 500)
    private String windowTitle;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "idle_seconds", nullable = false)
    private Integer idleSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "device_id")
    private UserDevice device;


}