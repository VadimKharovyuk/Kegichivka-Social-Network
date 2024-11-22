package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_listing_id")
    private JobListing jobListing;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long uniqueViewCount = 0L;

    @ElementCollection
    @CollectionTable(name = "daily_views",
            joinColumns = @JoinColumn(name = "statistics_id"))
    @MapKeyColumn(name = "view_date")
    @Column(name = "views_count")
    private Map<LocalDate, Integer> dailyViews = new HashMap<>();

    public void incrementViewCount() {
        this.viewCount++;
        LocalDate today = LocalDate.now();
        this.dailyViews.merge(today, 1, Integer::sum);
    }

    public void incrementUniqueViewCount() {
        this.uniqueViewCount++;
    }
}
