package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum EmploymentType {
    FULL_TIME("Полный день"),
    PART_TIME("Частичная занятость"),
    REMOTE("Удаленная работа"),
    FLEXIBLE("Гибкий график");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

}
