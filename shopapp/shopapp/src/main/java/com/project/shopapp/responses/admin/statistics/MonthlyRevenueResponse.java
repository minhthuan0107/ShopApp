package com.project.shopapp.responses.admin.statistics;

import com.project.shopapp.dtos.admin.statistics.MonthlyRevenueDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Data
public class MonthlyRevenueResponse {
    private List<MonthlyRevenueDto> monthlyRevenue;
    private BigDecimal totalRevenue;
}
