package com.example.kegichivka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ViewStatsSummaryDto {
    private Long totalViews;
    private Long totalUniqueViews;
    private Double averageViewTime;

    // Конструктор для использования в JPQL запросе
    public ViewStatsSummaryDto(Long totalViews, Long totalUniqueViews, Double averageViewTime) {
        this.totalViews = totalViews != null ? totalViews : 0L;
        this.totalUniqueViews = totalUniqueViews != null ? totalUniqueViews : 0L;
        this.averageViewTime = averageViewTime != null ? averageViewTime : 0.0;
    }

    // Вспомогательные методы для форматирования данных
    public String getFormattedTotalViews() {
        return String.format("%,d", totalViews);
    }

    public String getFormattedUniqueViews() {
        return String.format("%,d", totalUniqueViews);
    }

    public String getFormattedAverageViewTime() {
        return String.format("%.2f сек.", averageViewTime);
    }

    public Double getViewToApplicationRate() {
        return totalUniqueViews > 0 ? (double) totalViews / totalUniqueViews : 0.0;
    }
}