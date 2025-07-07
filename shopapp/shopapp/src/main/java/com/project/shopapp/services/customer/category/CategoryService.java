package com.project.shopapp.services.customer.category;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.category.CategoryDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, id)
                ));
    }
    @Override
    public List<Category> getAllCategories() {
        // LẤy danh sách category còn active
        return categoryRepository.findByIsDeletedFalse();
    }
}
