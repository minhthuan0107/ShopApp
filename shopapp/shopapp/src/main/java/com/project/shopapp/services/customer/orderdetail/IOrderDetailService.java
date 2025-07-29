package com.project.shopapp.services.customer.orderdetail;

import com.project.shopapp.responses.customer.orderdetail.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {
    OrderDetailResponse getOrderDetailById (Long orderDetailId) throws Exception;
    List<OrderDetailResponse> getOrderDetailsByOrderId (Long orderId) throws Exception;
    List<OrderDetailResponse> getAllOrderDetails();
    void deleteOrderDetailById (Long orderDetailId);
}
