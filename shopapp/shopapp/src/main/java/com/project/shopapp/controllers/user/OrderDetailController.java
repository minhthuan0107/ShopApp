package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.responses.customer.orderdetail.OrderDetailResponse;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.services.customer.orderdetail.OrderDetailService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orderdetails")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllOrderDetails() {
        List<OrderDetailResponse> orderDetailResponses = orderDetailService.getAllOrderDetails();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderDetailResponses)
                .message(localizationUtils.getLocalizedMessage(
                        MessageKeys.ORDERDETAIL_GET_ALL_ORDERDETAILS_SUCCESS))
                .build());
    }

    @GetMapping("/{orderDetailId}")
    public ResponseEntity<ResponseObject> getOrderDetailById(@Valid @PathVariable Long orderDetailId) {
        try {
            OrderDetailResponse orderDetailResponse = orderDetailService.getOrderDetailById(orderDetailId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(orderDetailResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.ORDERDETAIL_GET_BY_ID_SUCCESSFULLY, orderDetailResponse.getOrderDetailId()))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseObject> getOrderDetailsByOrderId(@Valid @PathVariable Long orderId) {
        try {
            List<OrderDetailResponse> orderDetailResponses = orderDetailService.getOrderDetailsByOrderId(orderId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(orderDetailResponses)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.ORDERDETAIL_FETCHED_SUCCESSFULLY, orderId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{orderDetailId}")
    public ResponseEntity<ResponseObject> deleteOrderDetail(@Valid @PathVariable Long orderDetailId) {
        orderDetailService.deleteOrderDetailById(orderDetailId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(
                        MessageKeys.ORDERDETAIL_DELETE_SUCCESSFULLY, orderDetailId))
                .build());
    }
}
