package com.focusmonitor.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class MessageReactionId implements Serializable {
    private static final long serialVersionUID = -3435994398246276731L;
    @NotNull
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Size(max = 10)
    @NotNull
    @Column(name = "emoji", nullable = false, length = 10)
    private String emoji;


}