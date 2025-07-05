package com.project.shopapp.services.admin.productimage;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.productimage.ProductImageDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImageAdminService implements IProductImageAdminService{
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Transactional
    @Override
    public ProductImage createProductImage(Long productId, ProductImageDto productImageDto) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, productId)
                        ));
        //Khong cho insert qua 5 anh cho 1 san pham
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Number of images must be <= "
                    + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDto.getImageUrl())
                .build();
        return productImageRepository.save(newProductImage);
    }
    @Transactional
    public void updateProductImages(Long productId, String imageUrl) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        product.setUrlImage(imageUrl);
        productRepository.save(product); // Lưu lại vào DB
    }
}
