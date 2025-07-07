package com.project.shopapp.services.customer.category;

import com.project.shopapp.dtos.customer.category.CategoryDto;
import com.project.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById  (long id) ;

    List<Category> getAllCategories();

}
