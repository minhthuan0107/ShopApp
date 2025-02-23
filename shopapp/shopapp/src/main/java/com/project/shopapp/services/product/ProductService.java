package com.project.shopapp.services.product;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDto;
import com.project.shopapp.dtos.ProductImageDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.response.product.ProductResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Product createProduct(ProductDto productDto) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, productDto.getCategoryId())
                        ));
        Product newProduct = Product.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .urlImage(productDto.getUrlImage())
                .quantity(productDto.getQuantity())
                .description(productDto.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductbyId(Long id) throws Exception {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, id)));
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        //Lay danh sach san pham theo trang (page) va gioi han(limit)
        return productRepository.findAll(pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    public Page<ProductResponse> getProductbyCategoryId(Long categoryId, PageRequest pageRequest) throws Exception {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, categoryId)
                        ));
        Page<Product> productPage = productRepository.findByCategory(existingCategory, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(
            Long id,
            ProductDto productDto) throws Exception {
        Product existingProduct = getProductbyId(id);
        if (existingProduct != null) {
            //Copy thuoc tinh Dto -> Product
            existingProduct.setName(productDto.getName());
            Category existingCategory = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() ->
                            new DataNotFoundException(
                                    localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, productDto.getCategoryId())));
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDto.getPrice());
            existingProduct.setDescription(productDto.getDescription());
            existingProduct.setUrlImage(productDto.getUrlImage());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(product -> productRepository.delete(product));
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }


}
