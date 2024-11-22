package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum ExperienceLevel {
    INTERN("Стажер", "Без опыта работы, подходит для начинающих карьеру", 0, 1),

    JUNIOR("Младший специалист", "Базовые знания и небольшой опыт работы", 1, 3),

    MIDDLE("Специалист", "Уверенные знания и опыт работы", 3, 5),

    SENIOR("Старший специалист", "Глубокие знания и богатый опыт работы", 5, 8),

    LEAD("Ведущий специалист", "Экспертные знания и опыт управления", 8, 12),

    EXPERT("Эксперт", "Исключительные знания и обширный опыт", 12, null);

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
