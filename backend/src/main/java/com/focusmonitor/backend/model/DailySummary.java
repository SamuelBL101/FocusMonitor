package com.focusmonitor.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "daily_summaries", indexes = {@Index(name = "idx_daily_summaries_user_date",
        columnList = "user_id, date")}, uniqueConstraints = {@UniqueConstraint(name = "daily_summaries_user_id_date_key",
        columnNames = {
                "user_id",
                "date"})})
public class DailySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_seconds", nullable = false)
    private Integer totalSeconds;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "productive_seconds", nullable = false)
    private Integer productiveSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "top_app_id")
    private AppDefinition topApp;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sessions_count", nullable = false)
    private Integer sessionsCount;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "computed_at", nullable = false)
    private Instant computedAt;


}