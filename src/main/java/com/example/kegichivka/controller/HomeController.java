package com.example.kegichivka.controller;

import com.example.kegichivka.dto.CategoryWithStatsDto;
import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.service.CategoryService;
import com.example.kegichivka.service.serviceImpl.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ArticleService articleService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            // Получаем категории
            List<CategoryWithStatsDto> categories = categoryService.getCategoriesWithStats();

            // Получаем статью
            ArticleDto latestArticle = articleService.getLatestArticle();

            // Определяем количество отображаемых категорий
            int totalCategories = categories.size();
            int displayedCategories = Math.min(totalCategories, 12);
            List<CategoryWithStatsDto> displayedList = categories.subList(0, displayedCategories);

            // Добавляем все в модель
            model.addAttribute("categories", displayedList);
            model.addAttribute("latestArticle", latestArticle);
            model.addAttribute("hasMoreCategories", totalCategories > displayedCategories);
            model.addAttribute("remainingCategoriesCount", totalCategories - displayedCategories);

            log.debug("Loaded {} categories, displaying {}", totalCategories, displayedCategories);

            return "home";
        } catch (Exception e) {
            log.error("Error loading home page", e);
            model.addAttribute("error", "Помилка завантаження сторінки");
            return "error";
        }
    }
}
