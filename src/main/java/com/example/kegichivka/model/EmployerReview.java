package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 1. Добавим рейтинг и отзывы для работодателей
@Entity
@Table(name = "employer_reviews")
@Getter
@Setter
public class EmployerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RegularUser reviewer;

    @ManyToOne
    private Company company;

    private Integer rating;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt;

    @OneToOne
    private JobApplication relatedApplication;
}
