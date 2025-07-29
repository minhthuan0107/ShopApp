package com.project.shopapp.services.admin.productimage;

import com.project.shopapp.dtos.customer.productimage.ProductImageDto;
import com.project.shopapp.models.ProductImage;

public interface IProductImageAdminService {
    ProductImage createProductImage (Long productId,
                                     ProductImageDto productImageDto) throws Exception;
    void updateProductImages(Long productId, String imageUrl) throws Exception;
}
