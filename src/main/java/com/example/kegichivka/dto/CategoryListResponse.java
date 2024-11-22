package com.example.kegichivka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentCategoryId;
    private List<CategoryListResponse> subCategories = new ArrayList<>();
}
