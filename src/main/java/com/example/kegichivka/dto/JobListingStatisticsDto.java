package com.example.kegichivka.dto;

import com.example.kegichivka.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListingStatisticsDto {
    private Long viewCount;
    private Long applicationsCount;
    private Map<ApplicationStatus, Long> applicationsByStatus;
}
