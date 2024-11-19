package com.example.kegichivka.model;

import com.example.kegichivka.enums.EmploymentType;
import com.example.kegichivka.model.abstracts.BaseListing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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
}
