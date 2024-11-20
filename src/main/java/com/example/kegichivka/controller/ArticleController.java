package com.example.kegichivka.controller;

import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.dto.article.CreateArticleRequest;
import com.example.kegichivka.dto.article.UpdateArticleRequest;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.model.Admin;
import com.example.kegichivka.service.AdminService;
import com.example.kegichivka.service.ImgurService;
import com.example.kegichivka.service.JwtService;
import com.example.kegichivka.service.UserPrincipal;
import com.example.kegichivka.service.serviceImpl.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final AdminService adminService;
    private final ImgurService imgurService;

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
                                @AuthenticationPrincipal UserPrincipal userPrincipal,
                                RedirectAttributes redirectAttributes) {
        try {
            if (userPrincipal.getRole() != UserRole.ADMIN) {
                throw new AccessDeniedException("Only admins can create articles");
            }

            Admin admin = adminService.findByEmail(userPrincipal.getEmail());
            List<String> uploadedPhotoUrls = new ArrayList<>();

            if (photos != null && !photos.isEmpty()) {
                for (MultipartFile photo : photos) {
                    if (!photo.isEmpty()) {
                        String photoUrl = imgurService.uploadImage(photo.getBytes());
                        if (photoUrl != null) {
                            uploadedPhotoUrls.add(photoUrl);
                            log.debug("Successfully uploaded photo: {}", photoUrl);
                        }
                    }
                }
            }

            request.setPhotoUrls(uploadedPhotoUrls);
            ArticleDto article = articleService.createArticle(request, admin);

            redirectAttributes.addFlashAttribute("successMessage", "Article created successfully!");
            return "redirect:/articles/" + article.getId();

        } catch (Exception e) {
            log.error("Error creating article", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create article: " + e.getMessage());
            return "redirect:/articles/create";
        }
    }
//    @PostMapping("/create")
//    public String createArticle(@ModelAttribute CreateArticleRequest request,
//                                @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
//                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
//        try {
//            if (userPrincipal.getRole() != UserRole.ADMIN) {
//                throw new AccessDeniedException("Only admins can create articles");
//            }
//
//            Admin admin = adminService.findByEmail(userPrincipal.getEmail());
//            List<String> uploadedPhotoUrls = new ArrayList<>();
//
//            if (photos != null && !photos.isEmpty()) {
//                for (MultipartFile photo : photos) {
//                    // Проверка размера файла (например, максимум 5MB)
//                    if (photo.getSize() > 5 * 1024 * 1024) {
//                        throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
//                    }
//
//                    // Проверка типа файла
//                    String contentType = photo.getContentType();
//                    if (contentType == null || !contentType.startsWith("image/")) {
//                        throw new IllegalArgumentException("Invalid file type. Only images are allowed");
//                    }
//
//                    if (!photo.isEmpty()) {
//                        String photoUrl = imgurService.uploadImage(photo.getBytes());
//                        if (photoUrl != null) {
//                            uploadedPhotoUrls.add(photoUrl);
//                            log.debug("Successfully uploaded photo: {}", photoUrl);
//                        }
//                    }
//                }
//            }
//
//            request.setPhotoUrls(uploadedPhotoUrls);
//            ArticleDto article = articleService.createArticle(request, admin);
//
//            return "redirect:/articles/" + article.getId();
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid input", e);
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        } catch (Exception e) {
//            log.error("Error creating article", e);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
//                    "Error creating article: " + e.getMessage());
//        }
//    }

//    @PostMapping("/create")
//    public String createArticle(@ModelAttribute CreateArticleRequest request,
//                                @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
//                                @AuthenticationPrincipal UserPrincipal userPrincipal) throws IOException {
//
//        // Проверяем роль
//        if (userPrincipal.getRole() != UserRole.ADMIN) {
//            throw new AccessDeniedException("Only admins can create articles");
//        }
//
//        // Получаем админа
//        Admin admin = adminService.findByEmail(userPrincipal.getEmail());
//
//        ArticleDto article = articleService.createArticle(request, admin);
//
//        if (photos != null && !photos.isEmpty()) {
//            List<byte[]> imagesData = photos.stream()
//                    .map(photo -> {
//                        try {
//                            return photo.getBytes();
//                        } catch (IOException e) {
//                            throw new RuntimeException("Failed to process photo", e);
//                        }
//                    })
//                    .toList();
//
//            articleService.addPhotosToArticle(article.getId(), imagesData, admin);
//        }
//
//        return "redirect:/articles/" + article.getId();
//    }



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
