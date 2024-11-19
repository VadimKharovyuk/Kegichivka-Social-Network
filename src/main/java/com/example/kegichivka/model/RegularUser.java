package com.example.kegichivka.model;

import com.example.kegichivka.model.abstracts.BaseUser;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "regular_users")
@Getter
@Setter
@NoArgsConstructor
public class RegularUser extends BaseUser {

    //предпочтения
    @ElementCollection
    private Set<String> preferences = new HashSet<>();

    @Column
    private String resume;

    @ElementCollection
    private Set<String> skills = new HashSet<>();


}
