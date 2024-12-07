package com.project.shopapp.services;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Order;
import com.project.shopapp.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder (OrderDto orderDto) throws Exception;
    OrderResponse getOrderById (long orderId) throws Exception;
    List<OrderResponse> getOrdersByUserId (long userId) throws Exception;
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrder (long OrderId, OrderDto orderDto) throws Exception;
    void deleteOrderById (long orderId);
}
