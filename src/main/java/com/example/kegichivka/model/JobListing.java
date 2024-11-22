package com.example.kegichivka.model;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.enums.ExperienceLevel;
import com.example.kegichivka.model.abstracts.BaseListing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//объявлений
@Entity
@Table(name = "job_listings")
@Getter
@Setter
public class JobListing extends BaseListing {
    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private String position;

    @Column
    private String requirements;

    @Column
    private String schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_user_id", nullable = false)
    private BusinessUser businessUser;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel requiredExperience;

    private Integer minYearsExperience;

    private Integer maxYearsExperience;

    private boolean remote;

    private String benefits;

    @ElementCollection
    @CollectionTable(name = "required_skills")
    private Set<String> requiredSkills = new HashSet<>();

    @OneToOne(mappedBy = "jobListing", cascade = CascadeType.ALL, orphanRemoval = true)
    private JobViewStatistics viewStatistics;

    @OneToMany(mappedBy = "jobListing", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplications = new ArrayList<>();

    @Embedded
    private Location location;

    private String metroStation;

    private String district;

    @ElementCollection
    @CollectionTable(name = "job_listing_photos")
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    public JobViewStatistics getOrCreateViewStatistics() {
        if (this.viewStatistics == null) {
            this.viewStatistics = new JobViewStatistics();
            this.viewStatistics.setJobListing(this);
            this.viewStatistics.setViewCount(0L);
            this.viewStatistics.setUniqueViewCount(0L);
        }
        return this.viewStatistics;
    }
    @ManyToMany
    @JoinTable(
            name = "job_listing_tags",
            joinColumns = @JoinColumn(name = "job_listing_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();


    @OneToMany(mappedBy = "jobListing", cascade = CascadeType.ALL)
    private List<JobApplication> applications = new ArrayList<>();
}
