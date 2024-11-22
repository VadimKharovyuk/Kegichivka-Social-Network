package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// 5. Добавим статистику просмотров вакансий
@Entity
@Table(name = "job_view_statistics")
@Getter
@Setter
public class JobViewStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private JobListing jobListing;

    private Integer viewCount;

    private Integer uniqueViewCount;

    @ElementCollection
    @CollectionTable(name = "daily_views")
    @MapKeyColumn(name = "view_date")
    private Map<LocalDate, Integer> dailyViews = new HashMap<>();
}
