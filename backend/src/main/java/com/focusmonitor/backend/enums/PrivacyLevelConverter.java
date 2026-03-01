package com.focusmonitor.backend.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PrivacyLevelConverter implements AttributeConverter<Privacy_level, String> {

    @Override
    public String convertToDatabaseColumn(Privacy_level attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public Privacy_level convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Privacy_level.valueOf(dbData.toUpperCase());
    }
}
