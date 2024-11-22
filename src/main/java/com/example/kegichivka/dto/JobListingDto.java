package com.example.kegichivka.dto;

import com.example.kegichivka.enums.ExperienceLevel;
import com.example.kegichivka.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListingDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal salary;
    private String position;
    private String requirements;
    private String schedule;
    private ExperienceLevel requiredExperience;
    private String experienceDescription; // Например: "3-5 лет"
    private Set<String> requiredSkills;
    private boolean isRemote;
    private String benefits;
    private Location location;
    // ... остальные поля
}
