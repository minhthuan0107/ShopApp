package com.project.shopapp.controllers.user;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductImageDto;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.responses.productimages.ProductImagesResponse;
import com.project.shopapp.services.product.ProductService;
import com.project.shopapp.services.productimage.ProductImageService;
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

@RestController
@RequestMapping("${api.prefix}/product-images")
public class ProductImageController {
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private Cloudinary cloudinary;

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

    //Upload lên PC
    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files,
                                          @PathVariable("id") Long productId) {
        try {
            ProductResponse existingProduct = productService.getProductbyId(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_ERROR_MAX_5_IMAGES));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                if (file != null) {
                    if (file.getSize() > 10 * 1024 * 1024) {
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_ERROR_FILE_LARGE));
                    }
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .body(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_ERROR_FILE_MUST_BE_IMAGE));
                    }
                    //Lưu file và cập nhật url_email trong DTO
                    String filename = storeFile(file);
                    ProductImage productImage = productImageService.createProductImage(
                            existingProduct.getId(),
                            ProductImageDto.builder()
                                    .imageUrl(filename)
                                    .build());
                    productImages.add(productImage);
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(productImages);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Api upload ảnh lên cloudinary
    @PostMapping(value = "uploadscloudinary/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImagesCloudinary(@RequestParam("files") List<MultipartFile> files,
                                                    @PathVariable("id") Long productId) {

        try {
            ProductResponse existingProduct = productService.getProductbyId(productId);
            files = files == null ? new ArrayList<>() : files;

            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_ERROR_MAX_5_IMAGES));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) continue;
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_ERROR_FILE_LARGE));
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_ERROR_FILE_MUST_BE_IMAGE));
                }
                // Upload file lên Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                // Lấy URL của ảnh từ Cloudinary
                String imageUrl = (String) uploadResult.get("secure_url");
                // Lưu URL vào database
                ProductImage productImage = productImageService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDto.builder()
                                .imageUrl(imageUrl)
                                .build());
                productImages.add(productImage);
            }
            // Cập nhật trường image của product với URL của ảnh đầu tiên
            if (!productImages.isEmpty()) {
                String firstImageUrl = productImages.get(0).getImageUrl(); // Lấy URL của ảnh đầu tiên
                existingProduct.setUrlImage(firstImageUrl);
                productImageService.updateProductImage(productId);// Cập nhật sản phẩm với ảnh đầu tiên
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(productImages);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Phương thức để lấy phần mở rộng của tệp
    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1 || lastIndexOfDot == fileName.length() - 1) {
            return ""; // Không có phần mở rộng
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException(localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_INVALID_IMAGE_FORMAT));
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //Thêm UUID vào trươc tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        java.nio.file.Path uploadDir = Paths.get("uploads");
        //Kiểm tra thư mục upload file đã tồn tại hay chưa
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        //Đường dẫn đầy đủ đến file
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
