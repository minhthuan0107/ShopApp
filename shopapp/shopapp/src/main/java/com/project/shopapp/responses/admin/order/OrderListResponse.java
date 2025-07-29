package com.project.shopapp.responses.admin.order;

import com.project.shopapp.responses.customer.order.OrderResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class OrderListResponse {
    private List<OrderResponse> orderResponses;
    private int totalPages;
    private long totalItems;
    private int currentPage;
}
