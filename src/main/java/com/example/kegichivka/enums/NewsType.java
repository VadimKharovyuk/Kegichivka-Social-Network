package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum NewsType {
    BUSINESS("Бизнес"),
    TECHNOLOGY("Технологии"),
    CAREER("Карьера"),
    INDUSTRY("Индустрия"),
    EDUCATION("Образование");

    private final String displayName;

    NewsType(String displayName) {
        this.displayName = displayName;
    }
}
