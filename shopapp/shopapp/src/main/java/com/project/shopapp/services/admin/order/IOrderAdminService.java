package com.project.shopapp.services.admin.order;

import com.project.shopapp.responses.customer.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IOrderAdminService {
    Page<OrderResponse> getAllOrders (PageRequest pageRequest, String keyword);

    public void updateOrderStatus (Long orderId) throws Exception;
}
