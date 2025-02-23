package com.project.shopapp.services.order;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.response.order.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder (OrderDto orderDto) throws Exception;
    OrderResponse getOrderById (long orderId) throws Exception;
    List<OrderResponse> getOrdersByUserId (long userId) throws Exception;
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrder (long OrderId, OrderDto orderDto) throws Exception;
    void deleteOrderById (long orderId);
}
