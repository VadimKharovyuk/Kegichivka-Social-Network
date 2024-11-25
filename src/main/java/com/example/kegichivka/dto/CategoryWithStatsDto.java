package com.example.kegichivka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithStatsDto {
    private Long id;
    private String name;
    private String description;
    private int jobCount;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}
