package com.project.shopapp.controllers.admin;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.product.ProductDto;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.admin.product.CreateProductResponse;
import com.project.shopapp.responses.product.ProductListResponse;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.services.admin.product.ProductAdminService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("${api.admin-prefix}/products")
public class AdminProductController {
    @Autowired
    private ProductAdminService productAdminService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductListResponse> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(required = false, defaultValue = "") String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<ProductResponse> productPage = productAdminService.getAllProducts(pageRequest, keyword);
        //Lấy tổng số sản phẩm
        int totalPages = productPage.getTotalPages();
        //Lấy tổng số khách hàng
        long totalItems = productPage.getTotalElements();
        List<ProductResponse> productResponseList = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .productResponses(productResponseList)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .currentPage(page + 1)
                .build());
    }

    @PostMapping(value = "/create-new")
    @PreAuthorize("hasRole('ADMIN')")
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
            Product newProduct = productAdminService.createProduct(productDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(CreateProductResponse.fromProduct(newProduct))
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

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @Valid @RequestBody ProductDto productDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            Product updateProduct = productAdminService.updateProduct(productId, productDto);
            return ResponseEntity.ok(ProductResponse.fromProduct(updateProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable("productId") Long productId) {
        try {
            productAdminService.deleteProductById(productId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.PRODUCT_DELETE_SUCCESSFULLY, productId))
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }
}
