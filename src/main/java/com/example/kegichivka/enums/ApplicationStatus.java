package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("На розгляді"),
    REVIEWING("У процесі перевірки"),
    INTERVIEW_SCHEDULED("Співбесіду призначено"),
    ACCEPTED("Заявку прийнято"),
    REJECTED("Заявку відхилено"),
    WITHDRAWN("Заявку відкликано");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
}