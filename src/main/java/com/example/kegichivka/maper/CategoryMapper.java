package com.example.kegichivka.maper;

import com.example.kegichivka.dto.CategoryDto;
import com.example.kegichivka.dto.CategoryListResponse;
import com.example.kegichivka.dto.CreateCategoryRequest;
import com.example.kegichivka.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    Category toEntity(CategoryDto categoryDto);

    CreateCategoryRequest toCreateRequest(CategoryDto categoryDto);
    CategoryDto toDto(CreateCategoryRequest createRequest);

    @Mapping(target = "parentCategoryId", source = "parentCategory.id")
    @Mapping(target = "subCategories", source = "subCategories")
    CategoryListResponse toCategoryListResponse(Category category);
}