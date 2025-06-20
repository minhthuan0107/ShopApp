package com.project.shopapp.components;

import com.project.shopapp.services.customer.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
@EnableScheduling
public class OrderCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OrderCleanupScheduler.class);
    @Autowired
    private OrderService orderService;
    // Chạy 3 tiếng để dọn dẹp đơn hàng spam order nếu người dùng bấm thanh toán và thoát ra lại trang order
    @Scheduled(fixedRate = 10800000)
    public void cleanUpPendingOrders() {
        int deletedOrders = orderService.deletePendingOnlineOrders();
        logger.info("Đã xóa {} đơn hàng PENDING chưa thanh toán.", deletedOrders);
    }
}