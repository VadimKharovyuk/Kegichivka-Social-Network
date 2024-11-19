package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private RegularUser user;

    @ManyToOne
    @JoinColumn(name = "job_listing_id", nullable = false)
    private JobListing jobListing;

    private LocalDateTime savedAt;

    // Опциональные заметки пользователя
    private String notes;

    // Желаемая дата отклика
    private LocalDate plannedApplyDate;

    @PrePersist
    protected void onCreate() {
        savedAt = LocalDateTime.now();
    }
}
