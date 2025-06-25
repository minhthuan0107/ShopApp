package com.project.shopapp.services.admin.statistics;

import com.project.shopapp.responses.admin.statistics.MonthlyRevenueResponse;

import java.math.BigDecimal;
import java.util.List;


public interface IStatisticsService {
    BigDecimal getTotalRevenueCurrentYear();

    BigDecimal getTotalRevenueCurrentMonth();

    MonthlyRevenueResponse getMonthlyRevenueByYear(int year);

    List<Integer> getAvailableOrderYears ();

    Long countPendingOrdersWithValidPayment();
}
