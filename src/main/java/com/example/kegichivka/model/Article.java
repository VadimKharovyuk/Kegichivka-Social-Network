package com.example.kegichivka.model;

import com.example.kegichivka.model.abstracts.BaseContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//статей
@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
public class Article extends BaseContent {


    @ElementCollection
    @CollectionTable(
            name = "article_photos",  // Изменили имя таблицы
            joinColumns = @JoinColumn(name = "article_id", nullable = false)  // Изменили имя колонки
    )
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();


    @ElementCollection
    private Set<String> tags = new HashSet<>();

    //краткое содержание
    @Column(length = 500)
    private String summary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    protected void onCreate() {
        super.onCreate();
        createdAt = LocalDateTime.now();
    }


}
