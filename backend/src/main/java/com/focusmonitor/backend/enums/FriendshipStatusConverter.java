package com.focusmonitor.backend.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FriendshipStatusConverter implements AttributeConverter<Friendship_status, String> {

    @Override
    public String convertToDatabaseColumn(Friendship_status attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public Friendship_status convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Friendship_status.valueOf(dbData.toLowerCase());
    }
}