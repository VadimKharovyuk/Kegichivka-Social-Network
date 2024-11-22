package com.example.kegichivka.model;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.enums.ExperienceLevel;
import com.example.kegichivka.model.abstracts.BaseListing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

//объявлений
@Entity
@Table(name = "job_listings")
@Getter
@Setter
@NoArgsConstructor
public class JobListing extends BaseListing {
    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private String position;

    @Column
    private String requirements;

    @Column
    private String schedule;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @ManyToMany
    @JoinTable(
            name = "job_listing_tags",
            joinColumns = @JoinColumn(name = "job_listing_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private ExperienceLevel requiredExperience;

    private Integer minYearsExperience;

    @Column(length = 1000)
    private String benefits;

    private boolean isRemote;



    @ElementCollection
    @CollectionTable(name = "required_skills")
    private Set<String> requiredSkills = new HashSet<>();


    // Опционально: максимальное количество лет опыта
    private Integer maxYearsExperience;

}
