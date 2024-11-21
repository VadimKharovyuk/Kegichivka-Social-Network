package com.example.kegichivka.repositoty;

import com.example.kegichivka.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthorId(Long authorId);
    Optional<Article> findFirstByOrderByPublishedAtDesc();
}
