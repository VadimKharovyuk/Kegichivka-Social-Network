package com.example.kegichivka.controller;

import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.dto.article.CreateArticleRequest;
import com.example.kegichivka.dto.article.UpdateArticleRequest;
import com.example.kegichivka.model.Admin;
import com.example.kegichivka.service.serviceImpl.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public String getAllArticles(Model model) {
        List<ArticleDto> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        return "articles/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createArticleRequest", new CreateArticleRequest());
        return "articles/create";
    }



    @PostMapping("/create")
    public String createArticle(@ModelAttribute CreateArticleRequest request,
                                @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
                                @AuthenticationPrincipal Admin admin) throws IOException {
        log.debug("Current admin: {}", admin); // добавляем логирование

        if (admin == null) {
            return "redirect:/login"; // перенаправляем на логин если пользователь не аутентифицирован
        }

        ArticleDto article = articleService.createArticle(request, admin);

        if (photos != null && !photos.isEmpty()) {
            List<byte[]> imagesData = photos.stream()
                    .map(photo -> {
                        try {
                            return photo.getBytes();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to process photo", e);
                        }
                    })
                    .toList();

            articleService.addPhotosToArticle(article.getId(), imagesData, admin);
        }

        return "redirect:/articles/" + article.getId();
    }

    @GetMapping("/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        ArticleDto article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "articles/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ArticleDto article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "articles/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateArticle(@PathVariable Long id,
                                @ModelAttribute UpdateArticleRequest request,
                                @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
                                @AuthenticationPrincipal Admin admin) throws IOException {
        articleService.updateArticle(id, request);

        if (photos != null && !photos.isEmpty()) {
            List<byte[]> imagesData = photos.stream()
                    .map(photo -> {
                        try {
                            return photo.getBytes();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to process photo", e);
                        }
                    })
                    .toList();

            articleService.addPhotosToArticle(id, imagesData, admin);
        }

        return "redirect:/articles/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    @PostMapping("/{id}/toggle-publish")
    public String togglePublishStatus(@PathVariable Long id) {
        articleService.togglePublishStatus(id);
        return "redirect:/articles/" + id;
    }

    @PostMapping("/{id}/like")
    public String likeArticle(@PathVariable Long id, @AuthenticationPrincipal Admin admin) {
        articleService.likeArticle(id, admin.getId());
        return "redirect:/articles/" + id;
    }

    @PostMapping("/{id}/unlike")
    public String unlikeArticle(@PathVariable Long id, @AuthenticationPrincipal Admin admin) {
        articleService.unlikeArticle(id, admin.getId());
        return "redirect:/articles/" + id;
    }
}
