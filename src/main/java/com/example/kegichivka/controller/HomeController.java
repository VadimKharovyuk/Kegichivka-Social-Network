package com.example.kegichivka.controller;

import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.service.serviceImpl.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ArticleService articleService;
    @GetMapping
    public String home(Model model) {
        ArticleDto latestArticle = articleService.getLatestArticle();
        model.addAttribute("latestArticle", latestArticle);
        return "home";
    }
}
