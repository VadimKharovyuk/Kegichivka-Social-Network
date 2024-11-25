package com.example.kegichivka.repositoty;

import com.example.kegichivka.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Поиск категорий верхнего уровня
    List<Category> findByParentCategoryIsNull();

    // Поиск подкатегорий
    List<Category> findByParentCategory(Category parentCategory);

    // Поиск по имени
    Optional<Category> findByName(String name);

    // Поиск категорий с активными вакансиями
    @Query("SELECT DISTINCT c FROM Category c " +
            "INNER JOIN JobListing j ON j.category = c " +
            "WHERE j.active = true")  // изменено с isActive на active
    List<Category> findCategoriesWithActiveJobs();



}
