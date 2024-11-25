package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum AccountStatus {
    PENDING_ACTIVATION("Очікує активації"),
    ACTIVE("Активний"),
    BLOCKED("Заблокований"),
    DELETED("Видалений");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }


}
