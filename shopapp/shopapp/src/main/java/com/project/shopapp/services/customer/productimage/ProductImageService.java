package com.project.shopapp.services.customer.productimage;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.customer.productimages.ProductImagesResponse;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageService implements IProductImageService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Override
    public List<ProductImagesResponse> getProductImagesByProductId(Long productId) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND,productId)));
        List<ProductImage> productImages = productImageRepository.findByProduct(existingProduct);
  //Trả về Response
        return productImages.stream()
                .map(image -> ProductImagesResponse.builder()
                        .id(image.getId())
                        .productId(image.getProduct().getId())
                        .urlImage(image.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
