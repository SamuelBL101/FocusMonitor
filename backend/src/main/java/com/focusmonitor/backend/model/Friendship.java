package com.focusmonitor.backend.model;

import com.focusmonitor.backend.enums.Friendship_status;
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
@Table(name = "friendships", indexes = {
        @Index(name = "idx_friendships_requester",
                columnList = "requester_id, status"),
        @Index(name = "idx_friendships_addressee",
                columnList = "addressee_id, status")}, uniqueConstraints = {@UniqueConstraint(name = "friendships_requester_id_addressee_id_key",
        columnNames = {
                "requester_id",
                "addressee_id"})})
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "addressee_id", nullable = false)
    private User addressee;

    @ColumnDefault("'pending'")
    @Column(name = "status", columnDefinition = "friendship_status", nullable = false)
    private Friendship_status status;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}