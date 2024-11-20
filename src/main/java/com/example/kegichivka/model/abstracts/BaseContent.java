package com.example.kegichivka.model.abstracts;

import com.example.kegichivka.model.Admin;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor
public abstract class BaseContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Admin author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;



    @Column
    private int likesCount = 0;

    @ElementCollection
    @CollectionTable(name = "content_likes")
    @Column(name = "user_id")
    private Set<Long> likedByUsers = new HashSet<>();

    @Column(nullable = false)
    private boolean isPublished = false;

    @PrePersist
    protected void onCreate() {
        publishedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
