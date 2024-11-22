package com.example.kegichivka.dto;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.enums.ExperienceLevel;
import com.example.kegichivka.enums.ListingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// JobListingResponseDto для полного ответа:
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListingResponseDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal salary;
    private String position;
    private String requirements;
    private String schedule;
    private EmploymentType employmentType;
    private ExperienceLevel requiredExperience;
    private Integer minYearsExperience;
    private Integer maxYearsExperience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private ListingStatus status;
    private LocationDto location;
    private String metroStation;
    private String district;
    private List<String> photoUrls;
    private boolean remote;
    private String benefits;
    private Set<String> requiredSkills;
    private CompanyDto company;
    private CategoryDto category;
    private Long applicationsCount;
    private Long viewsCount;

}
