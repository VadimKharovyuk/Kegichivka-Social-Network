package com.example.kegichivka.service;

import com.example.kegichivka.dto.CategoryDto;
import com.example.kegichivka.dto.CategoryListResponse;
import com.example.kegichivka.dto.CreateCategoryRequest;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.maper.CategoryMapper;
import com.example.kegichivka.model.Category;
import com.example.kegichivka.repositoty.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto createCategory(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        if (categoryDto.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParentCategory(parentCategory);
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    public CategoryDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toDto(category);
    }

    public List<CategoryListResponse> getAllCategories() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return rootCategories.stream()
                .map(categoryMapper::toCategoryListResponse)
                .collect(Collectors.toList());
    }
}



