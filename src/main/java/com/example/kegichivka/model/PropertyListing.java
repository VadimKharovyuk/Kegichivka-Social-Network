package com.example.kegichivka.model;


import com.example.kegichivka.enums.DealType;
import com.example.kegichivka.enums.PropertyType;
import com.example.kegichivka.model.abstracts.BaseListing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// Объявление об аренде/продаже недвижимости
@Entity
@Table(name = "property_listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyListing extends BaseListing {
    @Column(nullable = false)
    private BigDecimal price;


    @Column
    private Integer rooms;

    @Column
    private Integer floor;

    @Column
    private Double area;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType; // APARTMENT, HOUSE, STUDIO и т.д.

    @Enumerated(EnumType.STRING)
    private DealType dealType; // RENT, SALE
}
