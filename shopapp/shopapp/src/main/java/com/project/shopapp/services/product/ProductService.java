package com.project.shopapp.services.product;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDto;
import com.project.shopapp.dtos.ProductImageDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.BrandRepository;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.projection.PriceRangeProjection;
import com.project.shopapp.response.product.PriceRangeResponse;
import com.project.shopapp.response.product.ProductResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private BrandRepository brandRepository;

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
    public Product getProductbyId(Long productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, productId)));
    }
    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        //Lay danh sach san pham theo trang (page) va gioi han(limit)
        return productRepository.findAll(pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    public Page<ProductResponse> getProductbyCategoryId(
            Long categoryId,
            List<Long> brandIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            PageRequest pageRequest
    ) throws Exception {
        if (!categoryRepository.existsById(categoryId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, categoryId)
            );
        }
        if (brandIds != null && brandIds.isEmpty()) {
            brandIds = null;
        }
        Page<Product> productPage = productRepository.findByFilters(
                categoryId, brandIds, minPrice, maxPrice, pageRequest
        );
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    public Page<ProductResponse> searchProductsByKeyword(String keyword,
                                                         BigDecimal minPrice,
                                                         BigDecimal maxPrice,
                                                         PageRequest pageRequest)  {
        String[] words = keyword.trim().toLowerCase().split("\\s+");
        String likePattern = String.join("%", words);  // "samsung%s24"
       Page<Product> productPage = productRepository.searchByKeyword(
               likePattern, minPrice, maxPrice, pageRequest
        );
        return productPage.map(ProductResponse:: fromProduct);
    }

    @Override
    public List<ProductResponse> getProductSuggestions(String keyword) {
        String[] words = keyword.trim().toLowerCase().split("\\s+");
        // Tạo chuỗi tìm kiếm sử dụng dấu '%' giữa các từ
        String likePattern = String.join("%", words);
        List<Product> products = productRepository.getProductSuggestions(likePattern);

        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse :: fromProduct)
                .collect(Collectors.toList());
        return  productResponses;
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

    @Override
    public PriceRangeResponse getPriceRange() {
        PriceRangeProjection projection = productRepository.findMinAndMaxPrice();
        return new PriceRangeResponse(projection.getMinPrice(), projection.getMaxPrice());
    }
}
