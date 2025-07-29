package com.project.shopapp.services.customer.productimage;

import com.project.shopapp.responses.customer.productimages.ProductImagesResponse;

import java.util.List;

public interface IProductImageService {
    List<ProductImagesResponse> getProductImagesByProductId (Long productId) throws Exception;
}
