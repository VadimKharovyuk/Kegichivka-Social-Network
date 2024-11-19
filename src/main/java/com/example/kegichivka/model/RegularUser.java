package com.example.kegichivka.model;

import com.example.kegichivka.model.abstracts.BaseContent;
import com.example.kegichivka.model.abstracts.BaseUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "regular_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegularUser extends BaseUser {


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Resume> resumes = new ArrayList<>();
    //предпочтения
    @ElementCollection
    private Set<String> preferences = new HashSet<>();


    @Column
    private String resume;


    @ElementCollection
    private Set<String> skills = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SavedJob> savedJobs = new ArrayList<>();


}
