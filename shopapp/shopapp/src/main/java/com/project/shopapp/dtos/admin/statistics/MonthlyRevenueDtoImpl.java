package com.project.shopapp.dtos.admin.statistics;

import java.math.BigDecimal;

public class MonthlyRevenueDtoImpl implements MonthlyRevenueDto {
    private final Integer month;
    private final BigDecimal revenue;
    public MonthlyRevenueDtoImpl(Integer month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }


    @Override
    public Integer getMonth() {
        return month;
    }

    @Override
    public BigDecimal getRevenue() {
        return revenue;
    }
}
