package com.project.shopapp.responses.admin.coupon;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class CouponListResponse {
    private List<CouponResponse> couponResponses;
    private int totalPages;
    private long totalItems;
    private int currentPage;
}
