package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDto;
import com.project.shopapp.models.Category;
import com.project.shopapp.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Override
    public Category createCategory(CategoryDto categoryDto) {
        Category newCategory = Category.builder().
        name(categoryDto.getName()).
        build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDto categoryDto) {
         Category existingCategory = getCategoryById(categoryId);
         existingCategory.setName(categoryDto.getName());
         return  categoryRepository.save(existingCategory);
    }
    @Override
    public void deleteCategoryById(long id) {
     categoryRepository.deleteById(id);
    }
}
