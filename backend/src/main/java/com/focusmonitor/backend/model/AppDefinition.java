package com.focusmonitor.backend.model;

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
@Table(name = "app_definitions", uniqueConstraints = {@UniqueConstraint(name = "app_definitions_process_name_key",
        columnNames = {"process_name"})})
public class AppDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "process_name", nullable = false)
    private String processName;

    @Size(max = 500)
    @Column(name = "window_title_pattern", length = 500)
    private String windowTitlePattern;

    @Size(max = 255)
    @NotNull
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Size(max = 500)
    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "category_id")
    private AppCategory category;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "auto_classified", nullable = false)
    private Boolean autoClassified;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


}