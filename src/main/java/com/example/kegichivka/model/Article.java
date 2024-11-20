package com.example.kegichivka.model;

import com.example.kegichivka.model.abstracts.BaseContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


//статей
@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
public class Article extends BaseContent {


    @ElementCollection
    private Set<String> tags = new HashSet<>();

//краткое содержание
    @Column(length = 500)
    private String summary;
}
