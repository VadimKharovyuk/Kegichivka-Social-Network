package com.example.kegichivka.enums;

import lombok.Getter;
@Getter
public enum BusinessType {
    EMPLOYER("Роботодавець", "Розміщення вакансій"),
    REAL_ESTATE_RENT("Оренда нерухомості", "Здача нерухомості в оренду"),
    REAL_ESTATE_SALE("Продаж нерухомості", "Продаж нерухомості"),
    REAL_ESTATE_BOTH("Оренда і продаж", "Оренда і продаж нерухомості"),
    BUSINESS_ALL("Усі послуги", "Роботодавець та операції з нерухомістю");

    private final String type;
    private final String description;

    BusinessType(String type, String description) {
        this.type = type;
        this.description = description;
    }
}
