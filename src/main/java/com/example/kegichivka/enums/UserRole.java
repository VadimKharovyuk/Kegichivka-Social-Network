package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    REGULAR_USER("Звичайний користувач"),
    BUSINESS_USER("Бізнес користувач"),
    ADMIN("Адміністратор"),
    SUPER_ADMIN("Супер адміністратор");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}
