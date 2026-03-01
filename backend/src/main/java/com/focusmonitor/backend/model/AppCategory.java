package com.focusmonitor.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "app_categories", uniqueConstraints = {@UniqueConstraint(name = "app_categories_name_key",
        columnNames = {"name"})})
public class AppCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 7)
    @NotNull
    @ColumnDefault("'#6366f1'")
    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_productive", nullable = false)
    private Boolean isProductive;

    @Size(max = 100)
    @Column(name = "icon", length = 100)
    private String icon;


}