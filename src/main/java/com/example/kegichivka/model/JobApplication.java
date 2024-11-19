package com.example.kegichivka.model;

import com.example.kegichivka.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private RegularUser applicant;

    @ManyToOne
    private JobListing jobListing;

    @ManyToOne
    private Resume resume;

    @Column(length = 2000)
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime appliedAt;
}
