package com.project.shopapp.services.productimage;

import com.project.shopapp.dtos.ProductImageDto;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.responses.productimages.ProductImagesResponse;

import java.util.List;

public interface IProductImage {
    ProductImage createProductImage (Long productId,
                                     ProductImageDto productImageDto) throws Exception;
    ProductResponse updateProductImage(Long id) throws Exception;
    List<ProductImagesResponse> getProductImagesByProductId (Long productId) throws Exception;
}
