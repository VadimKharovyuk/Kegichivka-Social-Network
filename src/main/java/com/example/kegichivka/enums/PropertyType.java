package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum PropertyType {
    APARTMENT("Квартира"),
    STUDIO("Студія"),
    HOUSE("Будинок"),
    ROOM("Кімната"),
    PENTHOUSE("Пентхаус"),
    VILLA("Вілла"),
    COTTAGE("Котедж"),
    LOFT("Лофт"),
    GARAGE("Гараж"),
    COMMERCIAL("Комерційна нерухомість");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }
}
