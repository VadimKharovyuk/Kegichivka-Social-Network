package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RegularUser author;

    @ManyToOne
    private Company company;

    @Column(nullable = false)
    private int rating;

    //плюсы
    @Column(length = 1000)
    private String pros;
    @Column(length = 1000)
    private String cons;

    @Column
    private boolean isAnonymous;


    private LocalDateTime createdAt;
    private boolean isVerified;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
