package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.order.OrderDto;
import com.project.shopapp.responses.Object.ResponseObject;
import com.project.shopapp.responses.customer.order.OrderResponse;
import com.project.shopapp.services.customer.order.OrderService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("/{userId}")
    public ResponseEntity<?> placeOrder(@Valid @PathVariable Long userId,
                                        @RequestBody OrderDto orderDto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            OrderResponse orderResponse = orderService.placeOrder(userId, orderDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(orderResponse)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.ORDER_CREATED_SUCCESSFULLY, orderResponse.getOrderId()))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@Valid @PathVariable Long userId) {
        try {
            List<OrderResponse> orderResponses = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(orderResponses)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.ORDER_FETCHED_SUCCESSFULLY, userId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@Valid @PathVariable("orderId") long orderId) {
        try {
            OrderResponse orderResponse = orderService.getOrderById(orderId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(orderResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.GET_ORDER_BY_ID_SUCCESSFULLY, orderId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getAllOrders();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderResponses)
                .message(localizationUtils.getLocalizedMessage(
                        MessageKeys.GET_ALL_ORDERS_SUCCESSFULLY))
                .build());
    }

    @PutMapping("/{userId}/{orderId}")
    public ResponseEntity<?> updateOrder(@Valid @PathVariable("userId") Long userId,
                                         @PathVariable("orderId") Long orderId,
                                         @RequestBody OrderDto orderDto) {
        try {
            OrderResponse orderResponse = orderService.updateOrder(userId, orderId, orderDto);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(orderResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.ORDER_UPDATED_SUCCESSFULLY, orderId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PatchMapping("/cancel/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable("orderId") Long orderId) {
        try {
            //Xóa mềm cập nhật active = false
            orderService.deleteOrderById(orderId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.ORDER_DELETE_SUCCESSFULLY, orderId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
