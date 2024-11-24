package com.example.kegichivka.dto;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.enums.ExperienceLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 3. DTO для краткого представления вакансии (для списков)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JobListingShortDto {
    private Long id;
    private String title;
    private BigDecimal salary;
    private String position;
    private EmploymentType employmentType;
    private ExperienceLevel requiredExperience;
    private LocalDateTime createdAt;
    private LocationDto location;
    private boolean remote;
    private CompanyMinDto company;
    private Long applicationsCount;
}
