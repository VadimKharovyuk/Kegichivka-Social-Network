package com.example.kegichivka.service;


import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.dto.article.CreateArticleRequest;
import com.example.kegichivka.dto.article.UpdateArticleRequest;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.maper.article.ArticleAdminMapper;
import com.example.kegichivka.maper.article.ArticleMapper;
import com.example.kegichivka.model.Admin;
import com.example.kegichivka.model.Article;
import com.example.kegichivka.repositoty.ArticleRepository;
import com.example.kegichivka.service.serviceImpl.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final ArticleAdminMapper articleAdminMapper;
    private final ImgurService imgurService;

    public ArticleServiceImpl(
            ArticleRepository articleRepository,
            ArticleMapper articleMapper,
            ArticleAdminMapper articleAdminMapper,
            ImgurService imgurService) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.articleAdminMapper = articleAdminMapper;
        this.imgurService = imgurService;
    }

    public ArticleDto createArticle(CreateArticleRequest request, Admin admin) {
        try {
            Article article = articleMapper.toEntity(request);
            article.setAuthor(admin);

            // Инициализируем список, если он null
            if (article.getPhotoUrls() == null) {
                article.setPhotoUrls(new ArrayList<>());
            }

            // Добавляем URLs фотографий
            if (request.getPhotoUrls() != null) {
                article.getPhotoUrls().addAll(request.getPhotoUrls());
            }

            Article savedArticle = articleRepository.save(article);
            log.info("Created article with id: {} by admin: {}", savedArticle.getId(), admin.getEmail());

            return articleMapper.toDto(savedArticle);
        } catch (Exception e) {
            log.error("Error creating article", e);
            throw new RuntimeException("Failed to create article", e);
        }
    }

    public ArticleDto addPhotosToArticle(Long articleId, List<byte[]> imagesData, Admin admin) {
        try {
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

            if (!article.getAuthor().getId().equals(admin.getId())) {
                throw new AccessDeniedException("Not authorized to modify this article");
            }

            List<String> photoUrls = imgurService.uploadImages(imagesData);

            // Инициализируем список, если он null
            if (article.getPhotoUrls() == null) {
                article.setPhotoUrls(new ArrayList<>());
            }

            article.getPhotoUrls().addAll(photoUrls);

            Article savedArticle = articleRepository.save(article);
            return articleMapper.toDto(savedArticle);
        } catch (Exception e) {
            log.error("Error adding photos to article", e);
            throw new RuntimeException("Failed to add photos to article", e);
        }
    }

    @Override
    @Transactional
    public ArticleDto updateArticle(Long id, UpdateArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

//        articleMapper.updateArticleFromDto(request, article);
        Article updatedArticle = articleRepository.save(article);
        return articleMapper.toDto(updatedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        return articleMapper.toDto(article);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getArticlesByAuthor(Long authorId) {
        return articleRepository.findByAuthorId(authorId).stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ArticleDto togglePublishStatus(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        article.setPublished(!article.isPublished());
        Article updatedArticle = articleRepository.save(article);
        return articleMapper.toDto(updatedArticle);
    }

    @Override
    @Transactional
    public ArticleDto likeArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        if (article.getLikedByUsers().add(userId)) {
            article.setLikesCount(article.getLikesCount() + 1);
            Article updatedArticle = articleRepository.save(article);
            return articleMapper.toDto(updatedArticle);
        }
        return articleMapper.toDto(article);
    }

    @Override
    @Transactional
    public ArticleDto unlikeArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        if (article.getLikedByUsers().remove(userId)) {
            article.setLikesCount(article.getLikesCount() - 1);
            Article updatedArticle = articleRepository.save(article);
            return articleMapper.toDto(updatedArticle);
        }
        return articleMapper.toDto(article);
    }

    @Override
    @Transactional
    public ArticleDto addPhotoToArticle(Long articleId, byte[] imageData, Admin admin) throws AccessDeniedException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        if (!article.getAuthor().getId().equals(admin.getId())) {
            throw new AccessDeniedException("You don't have permission to add photos to this article");
        }

        String photoUrl = imgurService.uploadImage(imageData);
        if (photoUrl == null) {
            throw new RuntimeException("Failed to upload image to Imgur");
        }

        // Добавление фото в список photoUrls
        article.getPhotoUrls().add(photoUrl);

        // Сохраняем статью с фотографиями
        Article updatedArticle = articleRepository.save(article);

        return articleMapper.toDto(updatedArticle);
    }

    @Override
    @Transactional
    public ArticleDto removePhotoFromArticle(Long articleId, String photoUrl, Admin admin) throws AccessDeniedException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        if (!article.getAuthor().getId().equals(admin.getId())) {
            throw new AccessDeniedException("You don't have permission to remove photos from this article");
        }

//        article.getPhotoUrls().remove(photoUrl);
        Article updatedArticle = articleRepository.save(article);
        return articleMapper.toDto(updatedArticle);
    }
}
