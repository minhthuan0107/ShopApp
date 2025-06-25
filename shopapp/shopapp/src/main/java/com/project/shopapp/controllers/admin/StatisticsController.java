package com.project.shopapp.controllers.admin;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.admin.statistics.MonthlyRevenueResponse;
import com.project.shopapp.services.admin.statistics.StatisticsService;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("${api.admin-prefix}/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @GetMapping("/revenue/year")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTotalRevenueCurrentYear() {
        BigDecimal totalRevenue = statisticsService.getTotalRevenueCurrentYear();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(totalRevenue)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.STATISTICS_REVENUE_YEAR_SUCCESS, Year.now()))
                        .build());
    }

    @GetMapping("/revenue/month")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTotalRevenueCurrentMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        BigDecimal totalRevenue = statisticsService.getTotalRevenueCurrentMonth();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(totalRevenue)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.STATISTICS_REVENUE_MONTH_SUCCESS, currentMonth))
                        .build());
    }

    @GetMapping("{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getMonthlyRevenueByYear(@PathVariable int year) {
        MonthlyRevenueResponse totalRevenue = statisticsService.getMonthlyRevenueByYear(year);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(totalRevenue)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.STATISTICS_REVENUE_BY_YEAR_SUCCESS, year))
                        .build());
    }

    @GetMapping("available/year")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAvailableOrderYears() {
        List<Integer> availableOrderYears = statisticsService.getAvailableOrderYears();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(availableOrderYears)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.STATISTICS_AVAILABLE_ORDER_YEARS_SUCCESS))
                        .build());
    }

    @GetMapping("/count/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> countPendingOrdersWithValidPayment() {
        Long countOrder = statisticsService.countPendingOrdersWithValidPayment();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(countOrder)
                        .message(localizationUtils.getLocalizedMessage(
                                MessageKeys.STATISTICS_PENDING_ORDERS_COUNT_SUCCESS))
                        .build());
    }
}
