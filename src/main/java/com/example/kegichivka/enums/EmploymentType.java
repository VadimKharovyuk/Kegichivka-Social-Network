package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum EmploymentType {
    FULL_TIME("Повний робочий день"),
    PART_TIME("Часткова зайнятість"),
    REMOTE("Віддалена робота"),
    FLEXIBLE("Гнучкий графік");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }
}
