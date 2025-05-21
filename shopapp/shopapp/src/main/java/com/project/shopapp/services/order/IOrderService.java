package com.project.shopapp.services.order;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.responses.order.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse placeOrder (Long userId,OrderDto orderDto) throws Exception;
    OrderResponse getOrderById (Long orderId) throws Exception;
    List<OrderResponse> getOrdersByUserId (Long userId) throws Exception;
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrder (Long userId,Long orderId,OrderDto orderDto) throws Exception;
    void deleteOrderById (Long orderId);
    int deletePendingOnlineOrders() ;


}
