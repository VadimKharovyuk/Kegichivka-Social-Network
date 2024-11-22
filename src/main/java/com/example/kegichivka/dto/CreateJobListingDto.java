package com.example.kegichivka.dto;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.enums.ExperienceLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobListingDto {
    @NotBlank(message = "Заголовок обязателен")
    private String title;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotNull(message = "Зарплата обязательна")
    private BigDecimal salary;

    @NotBlank(message = "Должность обязательна")
    private String position;

    private String requirements;

    private String schedule;

    private Long categoryId;

    @NotNull(message = "Тип занятости обязателен")
    private EmploymentType employmentType;

    private ExperienceLevel requiredExperience;

    private Integer minYearsExperience;

    private Integer maxYearsExperience;

    @NotNull(message = "Локация обязательна")
    private LocationDto location;

    private String metroStation;

    private String district;

    private List<String> photoUrls;

    private boolean remote;

    private String benefits;

    private Set<String> requiredSkills;
}
