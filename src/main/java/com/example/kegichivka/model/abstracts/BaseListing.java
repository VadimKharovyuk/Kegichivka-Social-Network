package com.example.kegichivka.model.abstracts;

import com.example.kegichivka.enums.ListingStatus;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.Category;
import com.example.kegichivka.model.Location;
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

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_user_id", nullable = false)
    private BusinessUser businessUser;

    @Version
    private Long version;

    @Embedded
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private String metroStation;

    @Column
    private String district;

    @ElementCollection
    @CollectionTable(name = "listing_photos")
    private List<String> photoUrls = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
