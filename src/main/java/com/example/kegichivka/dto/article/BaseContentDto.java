package com.example.kegichivka.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseContentDto {
    private Long id;
    private AdminDto author;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private List<String> photoUrls;
    private int likesCount;
    private Set<Long> likedByUsers;
    private boolean isPublished;
}
