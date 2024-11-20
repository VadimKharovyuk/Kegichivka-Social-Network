package com.example.kegichivka.service.serviceImpl;

import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.dto.article.CreateArticleRequest;
import com.example.kegichivka.dto.article.UpdateArticleRequest;
import com.example.kegichivka.model.Admin;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ArticleService {
    ArticleDto createArticle(CreateArticleRequest request, Admin author);

    ArticleDto updateArticle(Long id, UpdateArticleRequest request);

    ArticleDto getArticleById(Long id);

    List<ArticleDto> getAllArticles();

    List<ArticleDto> getArticlesByAuthor(Long authorId);

    void deleteArticle(Long id);

    ArticleDto togglePublishStatus(Long id);

    ArticleDto likeArticle(Long articleId, Long userId);

    ArticleDto unlikeArticle(Long articleId, Long userId);
    ArticleDto addPhotoToArticle(Long articleId, byte[] imageData, Admin admin) throws AccessDeniedException;

    ArticleDto addPhotosToArticle(Long articleId, List<byte[]> imagesData, Admin admin) throws AccessDeniedException;

    ArticleDto removePhotoFromArticle(Long articleId, String photoUrl, Admin admin) throws AccessDeniedException;
}
