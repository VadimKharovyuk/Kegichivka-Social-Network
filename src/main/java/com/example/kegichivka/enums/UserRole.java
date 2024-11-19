package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    REGULAR_USER("Обычный пользователь"),
    BUSINESS_USER("Бизнес пользователь"),
    ADMIN("Администратор"),
    SUPER_ADMIN("Супер администратор");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

}
