package com.example.kegichivka.dto.admin;

import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.model.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private Admin admin;

    private List<ArticleDto> articles;
    // Можно добавить другие поля для статистики
    private Long totalUsers;
    private Long totalNews;
    private Long totalArticles;
    // и т.д.
}
