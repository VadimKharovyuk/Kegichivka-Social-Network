package com.example.kegichivka.model.abstracts;

import com.example.kegichivka.model.BusinessUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "business_user_id", nullable = false)
    private BusinessUser businessUser;

    // Список URL фотографий
    @ElementCollection
    @CollectionTable(name = "listing_photos")
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
