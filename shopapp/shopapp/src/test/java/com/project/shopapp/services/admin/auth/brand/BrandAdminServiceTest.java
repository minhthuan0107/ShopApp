package com.project.shopapp.services.admin.auth.brand;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.brand.BrandDto;
import com.project.shopapp.dtos.customer.category.CategoryDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.BrandRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.services.admin.brand.BrandAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

public class BrandAdminServiceTest {
    @Mock
    private BrandRepository brandRepository;

    @Mock
    private LocalizationUtils localizationUtils;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BrandAdminService brandAdminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    //Test tạo mới brand
    //Case 1 : Brand đã tồn tại
    @Test
    void createBrand_ShouldThrowException_WhenBrandAlreadyExists() {
        // given
        BrandDto dto = new BrandDto("Nike");
        Brand existingBrand = Brand.builder()
                .id(1L)
                .name("Nike")
                .isDeleted(false)
                .build();

        when(brandRepository.findByNameIgnoreCaseAndIsDeletedFalse(dto.getName()))
                .thenReturn(Optional.of(existingBrand));
        when(localizationUtils.getLocalizedMessage(MessageKeys.BRAND_ALREADY_EXISTS, "Nike"))
                .thenReturn("Brand Nike already exists");
        // when + then
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> brandAdminService.createBrand(dto));

        assertEquals("Brand Nike already exists", ex.getMessage());
        verify(brandRepository, never()).save(any());
    }
    //Case 2 : Brand bị xóa mềm → khôi phục
    @Test
    void createBrand_ShouldRestoreBrand_WhenBrandDeleted() throws Exception {
        // given
        BrandDto dto = new BrandDto("Adidas");
        Brand deletedBrand = Brand.builder()
                .id(2L)
                .name("Adidas")
                .isDeleted(true)
                .build();

        when(brandRepository.findByNameIgnoreCaseAndIsDeletedFalse("Adidas"))
                .thenReturn(Optional.empty());
        when(brandRepository.findByNameIgnoreCaseAndIsDeletedTrue("Adidas"))
                .thenReturn(Optional.of(deletedBrand));
        when(brandRepository.save(any(Brand.class))).thenAnswer(i -> i.getArgument(0));
        // when
        Brand result = brandAdminService.createBrand(dto);
        // then
        assertNotNull(result);
        assertEquals("Adidas", result.getName());
        assertFalse(result.isDeleted()); // Đã khôi phục
        verify(brandRepository, times(1)).save(deletedBrand);
    }
    //Case 3: Tạo mới Category
    @Test
    void createBrand_ShouldCreateNewBrand_WhenNotExists() throws Exception {
        // given
        BrandDto dto = new BrandDto("Adidas");

        when(brandRepository.findByNameIgnoreCaseAndIsDeletedFalse("Adidas"))
                .thenReturn(Optional.empty());
        when(brandRepository.findByNameIgnoreCaseAndIsDeletedTrue("Adidas"))
                .thenReturn(Optional.empty()); // Không có bị xóa mềm

        when(brandRepository.save(any(Brand.class)))
                .thenAnswer(i -> {
                    Brand saved = i.getArgument(0);
                    saved.setId(10L); // giả lập DB gán id
                    return saved;
                });

        // when
       Brand result = brandAdminService.createBrand(dto);

        // then
        assertNotNull(result);
        assertEquals("Adidas", result.getName());
        assertEquals(10L, result.getId()); // đã được "lưu"
        assertFalse(result.isDeleted());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }
    //Test xóa brand dựa trên id
    //Case 1: Brand không tồn tại
    @Test
    void deleteBrandById_ShouldThrowException_WhenBrandNotFound(){
        long brandId = 1L;
        when(brandRepository.findByIdAndIsDeletedFalse(brandId)).thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.BRAND_NOT_FOUND, brandId))
                .thenReturn("Brand not found with id "+brandId);
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> brandAdminService.deleteBrandById(brandId));
        assertEquals("Brand not found with id 1",ex.getMessage());
        verify(brandRepository, never()).save(any());
        verify(productRepository, never()).saveAll(any());
    }
    //Case 2 : Brand tồn tại và có product
    @Test
    void deleteBrandById_ShouldDeleteBrandAndProducts_WhenBrandHasProducts() throws Exception {
        long brandId = 2L;
        Brand brand = Brand.builder()
                .id(2L)
                .name("Adidas")
                .isDeleted(true)
                .build();
        Product product1 = Product.builder()
                .id(10L).name("Iphone 16")
                .isDeleted(false)
                .brand(brand).build();
        Product product2 = Product.builder()
                .id(10L).name("Iphone 17")
                .isDeleted(false)
                .brand(brand).build();
        List<Product> productList = Arrays.asList(product1,product2);
        when(brandRepository.findByIdAndIsDeletedFalse(brandId)).thenReturn(Optional.of(brand));
        when(productRepository.findByBrandAndIsDeletedFalse(brand)).thenReturn(productList);

        brandAdminService.deleteBrandById(brandId);
        assertTrue(brand.isDeleted());
        assertTrue(product1.isDeleted());
        assertTrue(product2.isDeleted());

        verify(brandRepository, times(1)).save(brand);
        verify(productRepository, times(1)).saveAll(productList);

    }
    //Case 3 : Brand tồn tại và không có product
    @Test
    void deleteBrandById_ShouldDeleteBrandOnly_WhenBrandHasNoProducts() throws Exception {
        long brandId = 3L;
        Brand brand = Brand
                .builder()
                .id(brandId)
                .name("Puma")
                .isDeleted(false)
                .build();

        when(brandRepository.findByIdAndIsDeletedFalse(brandId)).thenReturn(Optional.of(brand));
        when(productRepository.findByBrandAndIsDeletedFalse(brand)).thenReturn(Collections.emptyList());

        brandAdminService.deleteBrandById(brandId);

        assertTrue(brand.isDeleted());
        verify(brandRepository, times(1)).save(brand);
        verify(productRepository, never()).saveAll(any());
    }
}
