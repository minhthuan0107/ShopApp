package com.project.shopapp.services.customer.productimage;

import com.project.shopapp.dtos.customer.productimage.ProductImageDto;
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
