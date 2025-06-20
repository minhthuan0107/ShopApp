package com.project.shopapp.services.admin.statistics;

import com.project.shopapp.dtos.admin.statistics.MonthlyRevenueDto;
import com.project.shopapp.responses.admin.statistics.MonthlyRevenueResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


public interface IStatisticsService {
    BigDecimal getTotalRevenueCurrentYear();

    BigDecimal getTotalRevenueCurrentMonth();

    MonthlyRevenueResponse getMonthlyRevenueByYear(int year);

    List<Integer> getAvailableOrderYears ();

    Long countPendingOrdersWithValidPayment();
}
