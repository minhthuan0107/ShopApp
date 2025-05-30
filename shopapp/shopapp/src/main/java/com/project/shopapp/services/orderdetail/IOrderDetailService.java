package com.project.shopapp.services.orderdetail;

import com.project.shopapp.dtos.OrderDetailDto;
import com.project.shopapp.responses.orderdetail.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {
    OrderDetailResponse getOrderDetailById (Long orderDetailId) throws Exception;
    List<OrderDetailResponse> getOrderDetailsByOrderId (Long orderId) throws Exception;
    List<OrderDetailResponse> getAllOrderDetails();
    void deleteOrderDetailById (Long orderDetailId);
}
