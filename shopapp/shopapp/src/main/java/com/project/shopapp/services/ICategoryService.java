package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDto;
import com.project.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory (CategoryDto categoryDto);

    Category getCategoryById (long id);

    List<Category> getAllCategories();

    Category updateCategory (long categoryId, CategoryDto categoryDto);
    void deleteCategoryById (long id);
}
