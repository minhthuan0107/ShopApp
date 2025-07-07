package com.project.shopapp.services.admin.category;

import com.project.shopapp.dtos.customer.category.CategoryDto;
import com.project.shopapp.models.Category;

public interface ICategoryAdminService {
    Category createCategory(CategoryDto categoryDto) throws Exception;

    void deleteCategoryById  (long id) throws Exception;
}
