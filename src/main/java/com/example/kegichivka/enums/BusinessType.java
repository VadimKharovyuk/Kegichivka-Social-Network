package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum BusinessType {
    EMPLOYER("Работодатель", "Размещение вакансий"),
    REAL_ESTATE_RENT("Аренда недвижимости", "Сдача недвижимости в аренду"),
    REAL_ESTATE_SALE("Продажа недвижимости", "Продажа недвижимости"),
    REAL_ESTATE_BOTH("Аренда и продажа", "Аренда и продажа недвижимости"),
    BUSINESS_ALL("Все услуги", "Работодатель и операции с недвижимостью");

    private final String type;
    private final String description;

    BusinessType(String type, String description) {
        this.type = type;
        this.description = description;
    }

}
