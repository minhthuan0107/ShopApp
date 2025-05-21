package com.project.shopapp.services.productimage;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductImageDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.productimages.ProductImagesResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageService implements IProductImage {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Override
    @Transactional
    public Product updateProductImage(
            Long id, Product product) throws Exception {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, id)));
        return productRepository.save(existingProduct);
    }

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

}
