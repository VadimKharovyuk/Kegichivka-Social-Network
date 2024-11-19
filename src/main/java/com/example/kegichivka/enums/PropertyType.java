package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum PropertyType {
    APARTMENT("Квартира"),
    STUDIO("Студия"),
    HOUSE("Дом"),
    ROOM("Комната");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

}
