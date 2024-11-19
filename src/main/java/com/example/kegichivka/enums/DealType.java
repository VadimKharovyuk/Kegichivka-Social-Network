package com.example.kegichivka.enums;

import lombok.Getter;

@Getter
public enum DealType {
    RENT("Аренда"),
    SALE("Продажа");

    private final String displayName;

    DealType(String displayName) {
        this.displayName = displayName;
    }

}
