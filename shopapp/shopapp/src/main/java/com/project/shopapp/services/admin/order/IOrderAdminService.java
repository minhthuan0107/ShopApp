package com.project.shopapp.services.admin.order;

import com.project.shopapp.responses.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IOrderAdminService {
    Page<OrderResponse> getAllOrders (PageRequest pageRequest, String keyword);

    public void updateOrderStatus (Long orderId) throws Exception;
}
