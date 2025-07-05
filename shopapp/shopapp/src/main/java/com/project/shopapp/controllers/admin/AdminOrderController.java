package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.admin.order.OrderListResponse;
import com.project.shopapp.responses.order.OrderResponse;
import com.project.shopapp.services.admin.order.OrderAdminService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.admin-prefix}/orders")
public class AdminOrderController {
    @Autowired
    private OrderAdminService orderAdminService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderListResponse> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(required = false, defaultValue = "") String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<OrderResponse> orderPage = orderAdminService.getAllOrders(pageRequest, keyword);
        //Lấy tổng số trang
        int totalPages = orderPage.getTotalPages();
        //Lấy tổng số khách hàng
        long totalItems = orderPage.getTotalElements();
        List<OrderResponse> orderResponseList = orderPage.getContent();
        return ResponseEntity.ok(OrderListResponse.builder()
                .orderResponses(orderResponseList)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .currentPage(page + 1)
                .build());
    }

    @PatchMapping("/status/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateOrderStatus(@PathVariable Long orderId) {
        try {
            orderAdminService.updateOrderStatus(orderId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.ORDER_UPDATED_SUCCESSFULLY, orderId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
