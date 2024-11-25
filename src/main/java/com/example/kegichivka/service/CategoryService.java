package com.example.kegichivka.service;

import com.example.kegichivka.dto.CategoryDto;
import com.example.kegichivka.dto.CategoryListResponse;
import com.example.kegichivka.dto.CategoryWithStatsDto;
import com.example.kegichivka.dto.CreateCategoryRequest;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.maper.CategoryMapper;
import com.example.kegichivka.model.Category;
import com.example.kegichivka.repositoty.CategoryRepository;
import com.example.kegichivka.repositoty.JobListingRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final JobListingRepository listingRepository;

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



    public List<CategoryWithStatsDto> getCategoriesWithStats() {
        // Получаем категории верхнего уровня
        List<Category> parentCategories = categoryRepository.findByParentCategoryIsNull();

        return parentCategories.stream()
                .map(this::mapToCategoryWithStats)
                .collect(Collectors.toList());
    }

    private CategoryWithStatsDto mapToCategoryWithStats(Category category) {
        // Получаем количество активных вакансий для категории и её подкатегорий
        long jobCount = getJobCountForCategoryAndChildren(category);

        // Получаем диапазон зарплат
        SalaryRange salaryRange = getSalaryRangeForCategoryAndChildren(category);

        return CategoryWithStatsDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .jobCount((int) jobCount)
                .minSalary(salaryRange.getMinSalary())
                .maxSalary(salaryRange.getMaxSalary())
                .build();
    }

    private long getJobCountForCategoryAndChildren(Category category) {
        // Считаем вакансии в текущей категории
        long count = listingRepository.countByCategoryAndActiveTrue(category);

        // Добавляем вакансии из подкатегорий
        for (Category child : category.getSubCategories()) {
            count += getJobCountForCategoryAndChildren(child);
        }

        return count;
    }

    private SalaryRange getSalaryRangeForCategoryAndChildren(Category category) {
        BigDecimal minSalary = listingRepository.findMinSalaryByCategory(category);
        BigDecimal maxSalary = listingRepository.findMaxSalaryByCategory(category);

        // Проверяем подкатегории
        for (Category child : category.getSubCategories()) {
            SalaryRange childRange = getSalaryRangeForCategoryAndChildren(child);
            if (childRange.getMinSalary() != null &&
                    (minSalary == null || childRange.getMinSalary().compareTo(minSalary) < 0)) {
                minSalary = childRange.getMinSalary();
            }
            if (childRange.getMaxSalary() != null &&
                    (maxSalary == null || childRange.getMaxSalary().compareTo(maxSalary) > 0)) {
                maxSalary = childRange.getMaxSalary();
            }
        }

        return new SalaryRange(minSalary, maxSalary);
    }
}
@Data
@AllArgsConstructor
class SalaryRange {
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}



