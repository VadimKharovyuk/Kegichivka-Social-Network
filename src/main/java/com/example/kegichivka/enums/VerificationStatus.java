package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum VerificationStatus {
    UNVERIFIED("Не верифицирован"),
    EMAIL_VERIFICATION_SENT("Отправлено письмо"),
    EMAIL_VERIFIED("Email подтвержден"),
    DOCUMENTS_REQUIRED("Требуются документы"),
    DOCUMENTS_UPLOADED("Документы загружены"),
    UNDER_REVIEW("На проверке"),
    REJECTED("Отклонено"),
    VERIFIED("Подтвержден");

    private final String status;

    VerificationStatus(String status) {
        this.status = status;
    }

}
