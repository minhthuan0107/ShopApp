package com.project.shopapp.services.admin.product;

import com.project.shopapp.dtos.customer.product.ProductDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductAdminService {
    Page<ProductResponse> getAllProducts  (PageRequest pageRequest, String keyword);

    Product updateProduct (Long id, ProductDto productDto) throws Exception;

    Product createProduct(ProductDto productDto) throws DataNotFoundException;

    void deleteProductById (Long productId) throws Exception;
}
