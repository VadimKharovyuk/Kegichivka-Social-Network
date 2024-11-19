package com.example.kegichivka.model;

import com.example.kegichivka.enums.BusinessType;
import com.example.kegichivka.model.abstracts.BaseUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "business_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUser extends BaseUser {

    @OneToOne(cascade = CascadeType.ALL)
    private Company company;


    @Column(nullable = false)
    private String companyName;

    @Column
    private String companyDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    @Column
    private Double rating;

}