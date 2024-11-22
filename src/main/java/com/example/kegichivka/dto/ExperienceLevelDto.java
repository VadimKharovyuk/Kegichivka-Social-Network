package com.example.kegichivka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceLevelDto {
    private String level;
    private String displayName;
    private String description;
    private String yearRange;
    private Integer minYears;
    private Integer maxYears;
}
