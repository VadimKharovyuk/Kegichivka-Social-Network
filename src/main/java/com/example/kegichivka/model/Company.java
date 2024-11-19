package com.example.kegichivka.model;

import com.example.kegichivka.model.JobListing;
import com.example.kegichivka.model.abstracts.BaseListing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company extends BaseListing {
    private String website;
    private String industry;
    private int employeesCount;

    @Column(nullable = false)
    private boolean isVerified;
    private LocalDateTime verificationDate;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<JobListing> jobListings = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "hq_country")),
            @AttributeOverride(name = "city", column = @Column(name = "hq_city")),
            @AttributeOverride(name = "address", column = @Column(name = "hq_address")),
            @AttributeOverride(name = "latitude", column = @Column(name = "hq_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "hq_longitude"))
    })
    private Location headquarters;

    private String logo;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "company_social_links")
    @MapKeyColumn(name = "social_network")
    @Column(name = "profile_url")
    private Map<String, String> socialLinks = new HashMap<>();
}
