package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Фотографии для объявлений
@Entity
@Table(name = "listing_photos")
@Getter
@Setter
@NoArgsConstructor
public class ListingPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    @Column
    private String fileType;

    @Column
    private String description;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
}
