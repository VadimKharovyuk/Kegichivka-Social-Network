package com.example.kegichivka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatisticsDto {
    private Long viewCount;
    private Long uniqueViewCount;
    private Map<LocalDate, Integer> dailyViews;
    private Map<String, Integer> viewsByLocation;
    private Double averageViewTime;
}
