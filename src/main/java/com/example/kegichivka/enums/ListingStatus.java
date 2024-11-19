package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ListingStatus {
    PENDING("На проверке"),
    ACTIVE("Активно"),
    PAUSED("Приостановлено"),
    EXPIRED("Истекло"),
    ARCHIVED("В архиве"),
    REJECTED("Отклонено");

    private final String displayName;

    ListingStatus(String displayName) {
        this.displayName = displayName;
    }
}
