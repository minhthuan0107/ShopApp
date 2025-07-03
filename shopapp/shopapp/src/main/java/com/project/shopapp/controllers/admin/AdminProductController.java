package com.project.shopapp.controllers.admin;

import com.project.shopapp.models.Product;
import com.project.shopapp.responses.admin.order.OrderListResponse;
import com.project.shopapp.responses.order.OrderResponse;
import com.project.shopapp.responses.product.ProductListResponse;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.services.admin.product.ProductAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("${api.admin-prefix}/products")
public class AdminProductController {
    @Autowired
    private ProductAdminService productAdminService;
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
}
