package com.example.kegichivka.dto;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.enums.ExperienceLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchDto {
    private String keyword;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private List<EmploymentType> employmentTypes;
    private List<ExperienceLevel> experienceLevels;
    private String location;
    private Double radius; // в километрах для поиска по радиусу
    private List<Long> categoryIds;
    private Boolean isRemote;
    private LocalDateTime postedAfter;
    private List<Long> companyIds;
    private String sortBy;
    private String sortDirection;
}
