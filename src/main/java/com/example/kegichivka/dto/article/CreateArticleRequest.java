package com.example.kegichivka.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {
    private String title;
    private String content;
    private String summary;
    private Set<String> tags;
    private List<String> photoUrls = new ArrayList<>(); // Для хранения URL фотографий
    private LocalDateTime publishedAt;
}

