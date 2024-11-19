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
@Table(name = "admin_users")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends BaseUser {

    //разрешения
    @ElementCollection
    @Column(nullable = false)
    private Set<String> permissions = new HashSet<>();


}
