package com.example.kegichivka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private RegularUser user;

    @Column(nullable = false)
    private String position;

    @Column
    private String languages; // Владение языками

    @Column
    private boolean isRemoteWork;

    @OneToMany
    private List<SavedSearch> savedSearches; // Сохраненные поиски

    @Column
    private BigDecimal desiredSalary;

    @ElementCollection
    @CollectionTable(name = "resume_skills")
    private Set<String> skills = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resume_id")
    private List<WorkExperience> workExperience = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resume_id")
    private List<Education> educationHistory = new ArrayList<>();

    @Column(length = 2000)
    private String aboutMe;

    private boolean isVisible = true;
}
