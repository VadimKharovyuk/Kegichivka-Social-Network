package com.example.kegichivka.model;

import com.example.kegichivka.model.abstracts.BaseUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends BaseUser {

    //разрешения
    @ElementCollection
    @Column(nullable = false)
    private Set<String> permissions = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<News> news = new ArrayList<>();
}
