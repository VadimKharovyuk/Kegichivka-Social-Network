package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saved_searches")
public class SavedSearch {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private RegularUser user;

    private String keywords;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;

    @ManyToOne
    private Category category;

    @Embedded
    private Location location;

    private boolean emailNotifications;
}
