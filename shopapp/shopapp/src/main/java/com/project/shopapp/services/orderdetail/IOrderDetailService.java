package com.project.shopapp.services.orderdetail;

import com.project.shopapp.dtos.OrderDetailDto;
import com.project.shopapp.response.orderdetail.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {
    OrderDetailResponse createOrderDetail (OrderDetailDto orderDetailDto) throws Exception;
    OrderDetailResponse getOrderDetailById (long orderDetailId) throws Exception;
    List<OrderDetailResponse> getOrderDetailsByOrderId (long orderId) throws Exception;
    List<OrderDetailResponse> getAllOrderDetails();
    OrderDetailResponse updateOrderDetail (long orderDetailId, OrderDetailDto orderDetailDto) throws Exception;
    void deleteOrderDetailById (long orderDetailId);
}
