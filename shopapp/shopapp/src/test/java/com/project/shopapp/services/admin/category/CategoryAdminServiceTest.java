package com.project.shopapp.services.admin.category;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.category.CategoryDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.services.admin.category.CategoryAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryAdminServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private LocalizationUtils localizationUtils;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private CategoryAdminService categoryAdminService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test tạo mới category
    //Case 1: Category đã tồn tại
    @Test
    void createCategory_ShouldThrowException_WhenCategoryAlreadyExists() {
        CategoryDto categoryDto = new CategoryDto("Laptop");
        Category category = Category.builder()
                .id(1L)
                .name("Laptop")
                .isDeleted(false)
                .build();
        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedFalse(categoryDto.getName()))
                .thenReturn(Optional.of(category));
        when(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_ALREADY_EXISTS,categoryDto.getName()))
                .thenReturn("Category Laptop already exists");

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
        () -> categoryAdminService.createCategory(categoryDto));

        assertEquals("Category Laptop already exists", ex.getMessage());
        verify(categoryRepository, never()).save(any());
    }
    //Case 2: Category bị xóa mềm → khôi phục
    @Test
    void createBrand_ShouldRestoreBrand_WhenBrandDeleted() throws Exception {
        // given
        CategoryDto dto = new CategoryDto("Laptop");
        Category category = Category.builder()
                .id(2L)
                .name("Laptop")
                .isDeleted(true)
                .build();

        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedFalse("Laptop"))
                .thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedTrue("Laptop"))
                .thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        // when
        Category result = categoryAdminService.createCategory(dto);
        // then
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertFalse(result.isDeleted()); // Đã khôi phục
        verify(categoryRepository, times(1)).save(category);
    }
    //Case 3: Tạo mới Category
    @Test
    void createCategory_ShouldCreateNewCategory_WhenNotExists() throws Exception {
        // given
        CategoryDto dto = new CategoryDto("Tablet");

        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedFalse("Tablet"))
                .thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndIsDeletedTrue("Tablet"))
                .thenReturn(Optional.empty()); // Không có bị xóa mềm

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(i -> {
                    Category saved = i.getArgument(0);
                    saved.setId(10L); // giả lập DB gán id
                    return saved;
                });

        // when
        Category result = categoryAdminService.createCategory(dto);

        // then
        assertNotNull(result);
        assertEquals("Tablet", result.getName());
        assertEquals(10L, result.getId()); // đã được "lưu"
        assertFalse(result.isDeleted());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    //Test xóa category dựa trên id
    //Case 1: Category không tồn tại
    @Test
    void deleteCategoryById_ShouldThrowException_WhenCategoryNotFound(){
        long categoryId = 1L;
        when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, categoryId))
                .thenReturn("Category not found with id "+categoryId);
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> categoryAdminService.deleteCategoryById(categoryId));
        assertEquals("Category not found with id 1",ex.getMessage());
        verify(categoryRepository, never()).save(any());
        verify(productRepository, never()).saveAll(any());
    }
    //Case 2 : Category tồn tại và có product
    @Test
    void deleteCategoryById_ShouldDeleteCategoryAndProducts_WhenCategoryHasProducts() throws Exception {
        long categoryId = 2L;
       Category category = Category.builder()
                .id(2L)
                .name("Phone")
                .isDeleted(true)
                .build();
        Product product1 = Product.builder()
                .id(10L).name("Iphone 16")
                .isDeleted(false)
                .category(category)
                .build();
        Product product2 = Product.builder()
                .id(10L).name("Iphone 17")
                .isDeleted(false)
                .category(category)
                .build();
        List<Product> productList = Arrays.asList(product1,product2);
        when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryAndIsDeletedFalse(category)).thenReturn(productList);

        categoryAdminService.deleteCategoryById(categoryId);
        assertTrue(category.isDeleted());
        assertTrue(product1.isDeleted());
        assertTrue(product2.isDeleted());

        verify(categoryRepository, times(1)).save(category);
        verify(productRepository, times(1)).saveAll(productList);

    }
    //Case 3 : Category tồn tại và không có product
    @Test
    void deleteCategoryById_ShouldDeleteCategoryOnly_WhenCategoryHasNoProducts() throws Exception {
        long categoryId = 3L;
        Category category = Category
                .builder()
                .id(categoryId)
                .name("Laptop")
                .isDeleted(false)
                .build();

        when(categoryRepository.findByIdAndIsDeletedFalse(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryAndIsDeletedFalse(category)).thenReturn(Collections.emptyList());
        categoryAdminService.deleteCategoryById(categoryId);

        assertTrue(category.isDeleted());
        verify(categoryRepository, times(1)).save(category);
        verify(productRepository, never()).saveAll(any());
    }
}
