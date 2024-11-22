package com.example.kegichivka.model;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.enums.ExperienceLevel;
import com.example.kegichivka.model.abstracts.BaseListing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ Entity
@Table(name = "companies")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Company extends BaseListing {
    private String website;
    private String industry;
    private int employeesCount;

    @Column(nullable = false)
    private boolean verified;

    private LocalDateTime verificationDate;

    // Добавляем связь с вакансиями
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
    @CollectionTable(name = "company_social_links",
            joinColumns = @JoinColumn(name = "company_id"))
    @MapKeyColumn(name = "social_network")
    @Column(name = "profile_url")
    private Map<String, String> socialLinks = new HashMap<>();

    // Методы для управления вакансиями
    public void addJobListing(JobListing jobListing) {
        jobListings.add(jobListing);
        jobListing.setCompany(this);
    }

    public void removeJobListing(JobListing jobListing) {
        jobListings.remove(jobListing);
        jobListing.setCompany(null);
    }

    // Методы для управления отзывами
    public void addReview(Review review) {
        reviews.add(review);
        review.setCompany(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setCompany(null);
    }

    // Методы для социальных сетей
    public void addSocialLink(String network, String url) {
        socialLinks.put(network, url);
    }

    public void removeSocialLink(String network) {
        socialLinks.remove(network);
    }


    // Изменяем возвращаемый тип на int
    public int getActiveJobListingsCount() {
        if (jobListings == null) {
            return 0;
        }
        return (int) jobListings.stream()
                .filter(JobListing::isActive)
                .count();
    }

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public int getReviewsCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public int getTotalJobListings() {
        return jobListings != null ? jobListings.size() : 0;
    }

    // Дополнительные методы для анализа
    public Map<EmploymentType, Long> getJobsByEmploymentType() {
        if (jobListings == null) {
            return new EnumMap<>(EmploymentType.class);
        }
        return jobListings.stream()
                .filter(JobListing::isActive)
                .collect(Collectors.groupingBy(
                        JobListing::getEmploymentType,
                        () -> new EnumMap<>(EmploymentType.class),
                        Collectors.counting()
                ));
    }

    public Map<ExperienceLevel, Long> getJobsByExperienceLevel() {
        if (jobListings == null) {
            return new EnumMap<>(ExperienceLevel.class);
        }
        return jobListings.stream()
                .filter(JobListing::isActive)
                .collect(Collectors.groupingBy(
                        JobListing::getRequiredExperience,
                        () -> new EnumMap<>(ExperienceLevel.class),
                        Collectors.counting()
                ));
    }
}

