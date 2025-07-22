package com.project.shopapp.services.admin.product;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.product.ProductDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.BrandRepository;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductAdminService implements IProductAdminService{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest, String keyword) {
        Page<Product> productPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            //Nếu keyword rỗng thì lấy danh sách
            productPage = productRepository.findAllByIsDeletedFalse(pageRequest);
        } else {
            //Nếu có keyword thì tìm theo keyword
            productPage = productRepository.searchProductsByKeyword(keyword, pageRequest);
        }
        return productPage.map(product -> ProductResponse.fromProduct(product));
    }
    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDto productDto) throws Exception {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, id)));

        // Cập nhật category nếu categoryId khác
        if (productDto.getCategoryId() != null &&
                !productDto.getCategoryId().equals(existingProduct.getCategory().getId())) {
            Category existingCategory = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException(
                            localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, productDto.getCategoryId())));
            existingProduct.setCategory(existingCategory);
        }
        // Cập nhật brand nếu brandId khác
        if (productDto.getBrandId() != null &&
                !productDto.getBrandId().equals(existingProduct.getBrand().getId())) {
            Brand existingBrand = brandRepository.findById(productDto.getBrandId())
                    .orElseThrow(() -> new DataNotFoundException(
                            localizationUtils.getLocalizedMessage(MessageKeys.BRAND_NOT_FOUND, productDto.getBrandId())));
            existingProduct.setBrand(existingBrand);
        }
        // Cập nhật thông tin sản phẩm
        if (productDto.getName() != null) {
            existingProduct.setName(productDto.getName());
        }
        if (productDto.getPrice() != null) {
            existingProduct.setPrice(productDto.getPrice());
        }
        if (productDto.getQuantity() != null) {
            existingProduct.setQuantity(productDto.getQuantity());
        }
        if (productDto.getDescription() != null) {
            existingProduct.setDescription(productDto.getDescription());
        }
        if (productDto.getUrlImage() != null) {
            existingProduct.setUrlImage(productDto.getUrlImage());
        }
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public Product createProduct(ProductDto productDto) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(
                                        MessageKeys.CATEGORY_NOT_FOUND, productDto.getCategoryId())
                        ));
        Brand existingBrand = brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(
                                        MessageKeys.BRAND_NOT_FOUND, productDto.getBrandId())
                        ));
        Product newProduct = Product.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .urlImage(productDto.getUrlImage())
                .quantity(productDto.getQuantity())
                .description(productDto.getDescription())
                .category(existingCategory)
                .brand(existingBrand)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    @Transactional
    public void deleteProductById(Long productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND,productId)));
        product.setDeleted(true);
        productRepository.save(product);
    }
    @Override
    public List<ProductResponse> getTop10BestSellingProducts() {
        // Lấy danh sách productId từ 10 sản phẩm bán chạy
        List<Product> products = productRepository.findTop10BestSellingProducts();
        //Chuyển Product về productResponse
        List<ProductResponse> productResponses = products.stream().map(
                ProductResponse::fromProduct).collect(Collectors.toList());
        return productResponses;
    }
}
