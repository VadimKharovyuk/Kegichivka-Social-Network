package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("На рассмотрении"),
    REVIEWED("Просмотрено"),
    SHORTLISTED("В шортлисте"),
    INTERVIEW("Приглашен на интервью"),
    OFFER_SENT("Отправлено предложение"),
    ACCEPTED("Принят"),
    REJECTED("Отклонено");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
}