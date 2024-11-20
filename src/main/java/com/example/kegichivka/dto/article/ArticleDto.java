package com.example.kegichivka.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto extends BaseContentDto {
    private Long id;
    private String title;
    private String summary;
    private Set<String> tags = new HashSet<>(); // Добавляем поле tags
    private List<String> photoUrls = new ArrayList<>();
}
