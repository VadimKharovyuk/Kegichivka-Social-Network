package com.example.kegichivka.model;

import com.example.kegichivka.enums.NewsType;
import com.example.kegichivka.model.abstracts.BaseContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "news")
@Getter @Setter
@NoArgsConstructor
public class News extends BaseContent {
    @Column
    private boolean isBreaking = false;

    @Column
    private String source;

    @Column
    private String shortDescription;

    @Enumerated(EnumType.STRING)
    private NewsType type;

    @Column
    private int viewCount = 0;

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    @Column
    private int readingTimeMinutes;
}
