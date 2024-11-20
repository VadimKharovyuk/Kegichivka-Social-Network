package com.example.kegichivka.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto extends BaseContentDto {
    private Set<String> tags;
    private String summary;
}
