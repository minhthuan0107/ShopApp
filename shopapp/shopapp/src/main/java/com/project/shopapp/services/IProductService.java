package com.project.shopapp.services;
import com.project.shopapp.dtos.ProductDto;
import com.project.shopapp.dtos.ProductImageDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


public interface IProductService {
    Product createProduct(ProductDto productDto) throws DataNotFoundException;
    Product getProductbyId (Long id) throws Exception;
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Product updateProduct (Long id, ProductDto productDto) throws Exception;
    void deleteProduct (Long id);
    boolean existsByName (String name);
    ProductImage createProductImage (
            Long productId,
            ProductImageDto productImageDto) throws Exception;

}
