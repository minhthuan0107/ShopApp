package com.project.shopapp.services.category;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CategoryDto;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public Category createCategory(CategoryDto categoryDto) {
        Category newCategory = Category.builder().
                name(categoryDto.getName()).
                build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, id)
                ));
    }
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDto categoryDto) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDto.getName());
        return categoryRepository.save(existingCategory);
    }
    @Override
    public Category deleteCategoryById(long id) throws Exception {
        Category existingCategory = getCategoryById(id);
        List<Product> products = productRepository.findByCategory(existingCategory);
        if (!products.isEmpty()) {
            throw new RuntimeException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CANNOT_DELETE_CATEGORY_PRODUCT));
        } else {
            categoryRepository.deleteById(id);
            return existingCategory;
        }
    }
}
