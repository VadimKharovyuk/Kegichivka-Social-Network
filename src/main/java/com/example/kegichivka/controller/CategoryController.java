package com.example.kegichivka.controller;

import com.example.kegichivka.dto.CategoryDto;
import com.example.kegichivka.dto.CategoryListResponse;
import com.example.kegichivka.dto.CreateCategoryRequest;
import com.example.kegichivka.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        List<CategoryListResponse> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("categoryRequest", new CreateCategoryRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/create";
    }

    @PostMapping
    public String createCategory(@ModelAttribute CreateCategoryRequest request) {
        categoryService.createCategory(request);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        CategoryDto category = categoryService.getCategory(id);
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute CategoryDto categoryDto) {
        categoryService.updateCategory(id, categoryDto);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
