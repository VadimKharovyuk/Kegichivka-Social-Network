package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum AccountStatus {
    PENDING_ACTIVATION("Ожидает активации"),
    ACTIVE("Активен"),
    BLOCKED("Заблокирован"),
    DELETED("Удален");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }

}
