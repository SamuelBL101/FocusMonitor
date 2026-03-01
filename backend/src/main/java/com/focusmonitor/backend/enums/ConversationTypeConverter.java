package com.focusmonitor.backend.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConversationTypeConverter implements AttributeConverter<Conversation_type, String> {

    @Override
    public String convertToDatabaseColumn(Conversation_type attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public Conversation_type convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Conversation_type.valueOf(dbData.toLowerCase());
    }
}