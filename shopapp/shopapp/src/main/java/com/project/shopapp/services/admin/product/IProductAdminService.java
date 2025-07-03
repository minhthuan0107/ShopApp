package com.project.shopapp.services.admin.product;

import com.project.shopapp.responses.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductAdminService {
    Page<ProductResponse> getAllProducts  (PageRequest pageRequest, String keyword);
}
