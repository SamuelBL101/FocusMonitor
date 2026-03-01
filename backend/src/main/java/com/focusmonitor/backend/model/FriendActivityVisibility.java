package com.focusmonitor.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "friend_activity_visibility")
public class FriendActivityVisibility {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User users;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "show_current_app", nullable = false)
    private Boolean showCurrentApp;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "show_daily_stats", nullable = false)
    private Boolean showDailyStats;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "show_streak", nullable = false)
    private Boolean showStreak;


}