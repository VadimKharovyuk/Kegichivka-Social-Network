package com.example.kegichivka.dto.article;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
@Data
@Builder
public class UpdateArticleRequest {
    private String title;
    private String content;
    private List<String> photoUrls;
    private Set<String> tags;
    private String summary;
    private boolean isPublished;
}
