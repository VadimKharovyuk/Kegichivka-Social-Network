package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum NewsType {
    BUSINESS("Бізнес"),
    TECHNOLOGY("Технології"),
    CAREER("Кар'єра"),
    INDUSTRY("Індустрія"),
    EDUCATION("Освіта"),
    HEALTH("Охорона здоров'я"),
    SPORTS("Спорт"),
    ENTERTAINMENT("Розваги"),
    POLITICS("Політика"),
    SCIENCE("Наука"),
    ENVIRONMENT("Екологія"),
    CULTURE("Культура"),
    ART("Мистецтво"),
    FINANCE("Фінанси"),
    LIFESTYLE("Стиль життя");

    private final String displayName;

    NewsType(String displayName) {
        this.displayName = displayName;
    }
}
