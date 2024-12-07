package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.models.Order;
import com.project.shopapp.response.OrderResponse;
import com.project.shopapp.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto,
                                         BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
           OrderResponse orderResponse = orderService.createOrder(orderDto);
            return ResponseEntity.ok().body(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("user/{user_id}")
    public ResponseEntity<?> getOrdersByUserId (@Valid @PathVariable("user_id") long userId){
        try {
            List<OrderResponse> orderResponses = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById (@Valid @PathVariable("id") long orderId){
        try {
           OrderResponse orderResponse = orderService.getOrderById(orderId);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("")
    public ResponseEntity<?> getAllOrders (){
        try {
            List<OrderResponse> orderResponses = orderService.getAllOrders();
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder (@Valid @PathVariable("id") long id,
                                          @RequestBody OrderDto orderDto){
        try {
            OrderResponse orderResponse = orderService.updateOrder(id,orderDto);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("{id}")
    public  ResponseEntity<?> deleteOrder (@Valid @PathVariable("id") long orderId){
        //Xóa mềm cập nhật active = false
        orderService.deleteOrderById(orderId);
        return ResponseEntity.ok("Order  deleted successfully with ID = " +orderId);
    }
}
