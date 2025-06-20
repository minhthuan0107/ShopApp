package com.project.shopapp.services.admin.statistics;

import com.project.shopapp.dtos.admin.statistics.MonthlyRevenueDto;
import com.project.shopapp.dtos.admin.statistics.MonthlyRevenueDtoImpl;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.responses.admin.statistics.MonthlyRevenueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatisticsService implements IStatisticsService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public BigDecimal getTotalRevenueCurrentYear() {
        return Optional.ofNullable(orderRepository.getTotalRevenueOfCurrentYear())
                .orElse(BigDecimal.ZERO);

    }

    @Override
    public BigDecimal getTotalRevenueCurrentMonth() {
        return Optional.ofNullable(orderRepository.getTotalRevenueOfCurrentMonth())
                .orElse(BigDecimal.ZERO);

    }

    @Override
    public MonthlyRevenueResponse getMonthlyRevenueByYear(int year) {
        List<MonthlyRevenueDto> revenue = orderRepository.getMonthlyRevenueByYear(year);
        //Tạp mao từ kết quả raw:month -> revenue
        Map<Integer, BigDecimal> revenueMap = revenue.stream().
                collect(Collectors.toMap(
                        MonthlyRevenueDto::getMonth, MonthlyRevenueDto::getRevenue));
        List<MonthlyRevenueDto> fullResult = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            // Nếu map không có tháng này → lấy BigDecimal.ZERO
            BigDecimal revenueMonth = revenueMap.getOrDefault(month, BigDecimal.ZERO);
            // Sau đó tạo DTO chứa tháng + revenue
            fullResult.add(new MonthlyRevenueDtoImpl(month, revenueMonth));
        }
        // Tổng doanh thu từ danh sách đã đủ 12 tháng
        BigDecimal total = fullResult.stream()
                .map(MonthlyRevenueDto::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MonthlyRevenueResponse(fullResult,total);
    }

    @Override
    public List<Integer> getAvailableOrderYears() {
        return orderRepository.getAvailableOrderYears();
    }

    @Override
    public Long countPendingOrdersWithValidPayment() {
        return orderRepository.countPendingOrdersWithValidPayment();
    }
}
