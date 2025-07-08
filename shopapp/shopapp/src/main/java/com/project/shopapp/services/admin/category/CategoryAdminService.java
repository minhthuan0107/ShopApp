package com.project.shopapp.services.admin.category;

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
import java.util.Optional;

@Service
public class CategoryAdminService  implements ICategoryAdminService{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductRepository productRepository;
    @Override
    @Transactional
    public Category createCategory(CategoryDto categoryDto) throws Exception {
        String name = categoryDto.getName();
        // Kiểm tra nếu danh mục đã tồn tại và chưa bị xóa mềm → báo lỗi
        Optional<Category> existingCategoryOpt = categoryRepository.findByNameIgnoreCaseAndIsDeletedFalse(name);
        if (existingCategoryOpt.isPresent()) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_ALREADY_EXISTS, name)
            );
        }
        // Nếu danh mục bị xóa mềm → khôi phục
        Optional<Category> deletedCategoryOpt = categoryRepository.findByNameIgnoreCaseAndIsDeletedTrue(name);
        if (deletedCategoryOpt.isPresent()) {
            Category deletedCategory = deletedCategoryOpt.get();
            deletedCategory.setDeleted(false);
            return categoryRepository.save(deletedCategory);
        }
        // Tạo mới nếu chưa từng tồn tại
        Category newCategory = Category.builder()
                .name(name)
                .build();
        return categoryRepository.save(newCategory);
    }
    @Override
    @Transactional
    public void deleteCategoryById(long id) throws Exception {
        // Lấy category chưa bị xóa
        Category existingCategory = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, id)
                ));
        // Xóa mềm danh mục
        existingCategory.setDeleted(true);
        categoryRepository.save(existingCategory);
        // Lấy toàn bộ sản phẩm thuộc danh mục chưa bị xóa mềm
        List<Product> products = productRepository.findByCategoryAndIsDeletedFalse(existingCategory);
        // Xóa mềm toàn bộ sản phẩm thuộc danh mục này
        for (Product product : products) {
            product.setDeleted(true);
        }
        productRepository.saveAll(products);
    }
}
