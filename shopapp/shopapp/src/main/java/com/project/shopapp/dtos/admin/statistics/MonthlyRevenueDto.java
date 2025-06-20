package com.project.shopapp.dtos.admin.statistics;

import java.math.BigDecimal;

public interface MonthlyRevenueDto {
    //Interface-based Projection
    Integer getMonth();        // Số tháng (1–12)
    BigDecimal getRevenue();   // Tổng doanh thu
}
