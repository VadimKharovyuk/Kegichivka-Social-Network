package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ExperienceLevel {
    INTERN("Стажер", "Без досвіду роботи, підходить для початківців кар'єри", 0, 1),

    JUNIOR("Молодший спеціаліст", "Базові знання та невеликий досвід роботи", 1, 3),

    MIDDLE("Спеціаліст", "Впевнені знання та досвід роботи", 3, 5),

    SENIOR("Старший спеціаліст", "Глибокі знання та багатий досвід роботи", 5, 8),

    LEAD("Ведучий спеціаліст", "Експертні знання та досвід управління", 8, 12),

    EXPERT("Експерт", "Виключні знання та обширний досвід", 12, null);
    private final String displayName;
    private final String description;
    private final Integer minYears;
    private final Integer maxYears;

    ExperienceLevel(String displayName, String description, Integer minYears, Integer maxYears) {
        this.displayName = displayName;
        this.description = description;
        this.minYears = minYears;
        this.maxYears = maxYears;
    }


}
