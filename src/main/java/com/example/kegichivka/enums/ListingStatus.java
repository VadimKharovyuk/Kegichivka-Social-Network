package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ListingStatus {
    PENDING("На перевірці"),
    ACTIVE("Активно"),
    PAUSED("Призупинено"),
    EXPIRED("Строк вийшов"),
    ARCHIVED("В архіві"),
    REJECTED("Відхилено"),
    APPROVED("СХВАЛЕНО");

    private final String displayName;

    ListingStatus(String displayName) {
        this.displayName = displayName;
    }
}
