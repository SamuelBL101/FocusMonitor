package com.focusmonitor.backend.model;

import com.focusmonitor.backend.enums.Platform_type;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_devices", indexes = {@Index(name = "idx_devices_user",
        columnList = "user_id")})
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 255)
    @NotNull
    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", columnDefinition = "platform_type", nullable = false)
    private Platform_type platform;

    @Size(max = 50)
    @Column(name = "agent_version", length = 50)
    private String agentVersion;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;


}