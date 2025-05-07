package com.project.shopapp.controllers.user;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDto;
import com.project.shopapp.dtos.ProductImageDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.response.product.PriceRangeResponse;
import com.project.shopapp.response.product.ProductListResponse;
import com.project.shopapp.response.product.ProductResponse;
import com.project.shopapp.services.product.ProductService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createAt").descending());
                Sort.by("id").ascending());
        Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);
        //Lấy tổng số trang
        int totalPages = productPage.getTotalPages();
        //Lấy tổng số sản phẩm
        long totalItems = productPage.getTotalElements();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .productResponses(products)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .build());
    }

    @GetMapping("category/{categoryId}")
    public ResponseEntity<?> getProductByCategoryId(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "brandIds", required = false) String brandIdsStr,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        try {

            List<Long> brandIds = new ArrayList<>();
            if (brandIdsStr != null && !brandIdsStr.isEmpty()) {
                brandIds = Arrays.stream(brandIdsStr.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            }
            // Xử lý tham số sắp xếp
            String[] sortParams = sort.split(",");
            String sortField = sortParams.length > 0 ? sortParams[0] : "id";
            Sort.Direction sortDirection = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            Sort sortObj = Sort.by(sortDirection, sortField);
            PageRequest pageRequest = PageRequest.of(page, limit, sortObj);
            Page<ProductResponse> productPage = productService.getProductbyCategoryId(
                    categoryId, brandIds, minPrice, maxPrice, pageRequest);

            return ResponseEntity.ok(ProductListResponse.builder()
                    .productResponses(productPage.getContent())
                    .totalPages(productPage.getTotalPages())
                    .totalItems(productPage.getTotalElements())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseObject> getProductById(@PathVariable("productId") Long productId) {
        try {
            Product existingProduct = productService.getProductbyId(productId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(ProductResponse.fromProduct(existingProduct))
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.PRODUCT_GET_BY_ID_SUCCESSFULLY, productId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }
    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDto productDto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            Product newProduct = productService.createProduct(productDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(ProductResponse.fromProduct(newProduct))
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.PRODUCT_CREATED_SUCCESSFULLY, newProduct.getId()))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           ProductDto productDto) {
        try {
            Product updateProduct = productService.updateProduct(id, productDto);
            return ResponseEntity.ok(ProductResponse.fromProduct(updateProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(
                        MessageKeys.PRODUCT_DELETE_SUCCESSFULLY, id))
                .build());
    }

    @GetMapping("/price-range")
    public ResponseEntity<ResponseObject> getPriceRange() {
        PriceRangeResponse priceRange = productService.getPriceRange();
        return ResponseEntity.status(HttpStatus.OK).
                body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(priceRange)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.VALUE_MIN_MAX_SUCCESSFULLY))
                        .build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
            // Xử lý sắp xếp
            String[] sortParams = sort.split(",");
            String sortField = sortParams.length > 0 ? sortParams[0] : "id";
            Sort.Direction sortDirection = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            Sort sortObj = Sort.by(sortDirection, sortField);
            PageRequest pageRequest = PageRequest.of(page, limit, sortObj);
            // Gọi service tìm kiếm sản phẩm theo từ khóa và khoảng giá
            Page<ProductResponse> productPage = productService.searchProductsByKeyword(
                    keyword, minPrice, maxPrice, pageRequest);
            return ResponseEntity.ok(ProductListResponse.builder()
                    .productResponses(productPage.getContent())
                    .totalPages(productPage.getTotalPages())
                    .totalItems(productPage.getTotalElements())
                    .build());
        }

    @GetMapping("/suggestions")
    public ResponseEntity<ResponseObject> getProductSuggestions(@RequestParam String keyword) {
            List<ProductResponse> suggestedProducts = productService.getProductSuggestions(keyword);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(suggestedProducts)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.PRODUCT_SUGGESTIONS_GET_SUCCESSFULLY, keyword))
                    .build());
    }
}
