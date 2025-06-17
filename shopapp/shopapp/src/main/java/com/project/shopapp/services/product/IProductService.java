package com.project.shopapp.services.product;
import com.project.shopapp.dtos.ProductDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.product.PriceRangeResponse;
import com.project.shopapp.responses.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;


public interface IProductService {
    Product createProduct(ProductDto productDto) throws DataNotFoundException;
    ProductResponse getProductbyId (Long productId) throws Exception;
    Page<ProductResponse> getProductbyCategoryId(
            Long categoryId,
            List<Long> brandIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            PageRequest pageRequest
    ) throws Exception;

    Page<ProductResponse> searchProductsByKeyword(
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            PageRequest pageRequest
    );
    List<ProductResponse>  getProductSuggestions (String keyword);
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Product updateProduct (Long id, ProductDto productDto) throws Exception;
    void deleteProduct (Long id);
    boolean existsByName (String name);
    PriceRangeResponse getPriceRange ();
    List<ProductResponse>  getTop14BestSellingProducts();

    List<ProductResponse>  getTop14MostHighlyRatedProducts();



}
