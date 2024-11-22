package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("На рассмотрении"),
    REVIEWING("В процессе проверки"),
    INTERVIEW_SCHEDULED("Собеседование назначено"),
    ACCEPTED("Заявка принята"),
    REJECTED("Заявка отклонена"),
    WITHDRAWN("Заявка отозвана");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
}