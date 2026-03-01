package com.focusmonitor.backend.model;

import com.focusmonitor.backend.enums.Privacy_level;
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
@Table(name = "user_settings", uniqueConstraints = {@UniqueConstraint(name = "user_settings_user_id_key",
        columnNames = {"user_id"})})
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ColumnDefault("240")
    @Column(name = "daily_goal_minutes", nullable = false)
    private Integer dailyGoalMinutes;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "tracking_enabled", nullable = false)
    private Boolean trackingEnabled;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "notifications_enabled", nullable = false)
    private Boolean notificationsEnabled;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'friends'")
    @Column(name = "privacy_level", columnDefinition = "privacy_level", nullable = false)
    private Privacy_level privacyLevel;


}