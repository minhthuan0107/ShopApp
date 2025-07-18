package com.project.shopapp.controllers.user;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.productimage.ProductImageDto;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.responses.productimages.ProductImagesResponse;
import com.project.shopapp.services.customer.product.ProductService;
import com.project.shopapp.services.customer.productimage.ProductImageService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/product-images")
public class ProductImageController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;


    @GetMapping("{productId}")
    public ResponseEntity<ResponseObject> getProductImagesByProductId(@PathVariable("productId") Long productId) {
        try {
            List<ProductImagesResponse> productImages = productImageService.getProductImagesByProductId(productId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(productImages)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.PRODUCTIMAGES_GET_BY_PRODUCT_ID_SUCCESSFULLY, productId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }


}
