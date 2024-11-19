package com.example.kegichivka.model;

import com.example.kegichivka.enums.BusinessType;
import com.example.kegichivka.model.abstracts.BaseUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "business_users")
@Getter
@Setter
@NoArgsConstructor
public class BusinessUser extends BaseUser {
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