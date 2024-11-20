package com.example.kegichivka.maper.article;

import com.example.kegichivka.dto.*;
import com.example.kegichivka.dto.article.ArticleDto;
import com.example.kegichivka.dto.article.CreateArticleRequest;
import com.example.kegichivka.dto.article.UpdateArticleRequest;
import com.example.kegichivka.model.*;
import org.mapstruct.*;

import java.util.ArrayList;

@Mapper(componentModel = "spring", uses = {ArticleAdminMapper.class})
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "likedByUsers", ignore = true)
    @Mapping(target = "photoUrls", source = "photoUrls")
    @Mapping(target = "tags", source = "tags")
    Article toEntity(CreateArticleRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "summary", source = "summary")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "photoUrls", source = "photoUrls")
    ArticleDto toDto(Article article);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "likedByUsers", ignore = true)
    void updateArticleFromDto(UpdateArticleRequest request, @MappingTarget Article article);
}

