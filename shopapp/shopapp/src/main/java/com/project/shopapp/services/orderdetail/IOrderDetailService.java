package com.project.shopapp.services.orderdetail;

import com.project.shopapp.dtos.OrderDetailDto;
import com.project.shopapp.response.orderdetail.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {
    OrderDetailResponse getOrderDetailById (Long orderDetailId) throws Exception;
    List<OrderDetailResponse> getOrderDetailsByOrderId (Long orderId) throws Exception;
    List<OrderDetailResponse> getAllOrderDetails();
    OrderDetailResponse updateOrderDetail (Long orderId,Long orderDetailId,OrderDetailDto orderDetailDto) throws Exception;
    void deleteOrderDetailById (Long orderDetailId);
}
