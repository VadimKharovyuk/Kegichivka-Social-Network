package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum VerificationStatus {
    UNVERIFIED("Не верифіковано"),
    EMAIL_VERIFICATION_SENT("Відправлено лист"),
    EMAIL_VERIFIED("Email підтверджено"),
    DOCUMENTS_REQUIRED("Потрібні документи"),
    DOCUMENTS_UPLOADED("Документи завантажено"),
    UNDER_REVIEW("На перевірці"),
    REJECTED("Відхилено"),
    VERIFIED("Підтверджено");

    private final String status;

    VerificationStatus(String status) {
        this.status = status;
    }
}
