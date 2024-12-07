package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDto;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.response.OrderDetailResponse;
import com.project.shopapp.response.OrderResponse;

import java.util.List;

public interface IOrderDetailService {
    OrderDetailResponse createOrderDetail (OrderDetailDto orderDetailDto) throws Exception;
    OrderDetailResponse getOrderDetailById (long orderDetailId) throws Exception;
    List<OrderDetailResponse> getOrderDetailsByOrderId (long orderId) throws Exception;
    List<OrderDetailResponse> getAllOrderDetails();
    OrderDetailResponse updateOrderDetail (long orderDetailId, OrderDetailDto orderDetailDto) throws Exception;
    void deleteOrderDetailById (long orderDetailId);
}
