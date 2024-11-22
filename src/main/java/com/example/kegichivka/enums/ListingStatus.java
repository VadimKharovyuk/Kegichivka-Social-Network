package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ListingStatus {
    PENDING("На проверке"),
    ACTIVE("Активно"),
    PAUSED("Приостановлено"),
    EXPIRED("Истекло"),
    ARCHIVED("В архиве"),
    REJECTED("Отклонено"),
    APPROVED("ОДОБРЕННЫЙ");

    private final String displayName;

    ListingStatus(String displayName) {
        this.displayName = displayName;
    }
}
