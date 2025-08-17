package com.project.shopapp.repositories;

import com.project.shopapp.dtos.admin.statistics.MonthlyRevenueDto;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.shippingInfo.trackingNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Order> searchOrdersByKeyword(@Param("keyword") String keyword, Pageable pageable);

    //Xóa order và payment giá trị pending tránh spam order
    @Modifying
    @Transactional
    @Query(value = """
                DELETE o FROM orders o
                JOIN payments p ON o.id = p.order_id
                WHERE o.status = 'PENDING'
                AND p.payment_method IN :paymentMethods
                AND p.status = 'PENDING'
                AND p.create_at < DATE_SUB(NOW(), INTERVAL 20 MINUTE)
            """, nativeQuery = true)
    int deletePendingOnlineOrders(@Param("paymentMethods") List<String> paymentMethods);



    @Query(value = "SELECT SUM(o.total_price) FROM orders o " +
            "JOIN payments p ON o.id = p.order_id " +
            "WHERE YEAR(o.order_date) = YEAR(NOW()) " +
            "AND p.status='SUCCESS' " +
            "AND o.status IN ('PENDING', 'PROCESSING','SHIPPING', 'COMPLETED')",nativeQuery = true)
    BigDecimal getTotalRevenueOfCurrentYear();

    @Query(value = "SELECT SUM(o.total_price) FROM orders o " +
            "JOIN payments p ON o.id = p.order_id " +
            "WHERE MONTH(o.order_date) = MONTH(NOW()) " +
            "AND p.status='SUCCESS' " +
            "AND o.status IN ('PENDING','PROCESSING','SHIPPING', 'COMPLETED')",nativeQuery = true)
    BigDecimal getTotalRevenueOfCurrentMonth();

    @Query(value = "SELECT MONTH(o.order_date) as month ," +
            "SUM(o.total_price) revenue " +
            "FROM orders o " +
            "JOIN payments p ON o.id = p.order_id " +
            "WHERE YEAR(o.order_date) = :year " +
            "AND p.status = 'SUCCESS' " +
            "AND o.status IN ('PENDING','PROCESSING','SHIPPING','COMPLETED') " +
            "GROUP BY MONTH(o.order_date) " +
            "ORDER BY MONTH(o.order_date) ", nativeQuery = true)
    List<MonthlyRevenueDto> getMonthlyRevenueByYear(@Param("year") int year);

    @Query(value = "SELECT DISTINCT YEAR(o.order_date) " +
            "FROM orders o " +
            "JOIN payments p ON o.id = p.order_id " +
            "WHERE p.status = 'SUCCESS' " +
            "AND o.status IN ('PENDING','PROCESSING','SHIPPING','COMPLETED') " +
            "ORDER BY YEAR(o.order_date) DESC " ,nativeQuery = true)
    List<Integer> getAvailableOrderYears();

    @Query(value = "SELECT COUNT(*) " +
            "FROM orders o " +
            "JOIN payments p ON o.id = p.order_id " +
            "WHERE o.status = 'PENDING' " +
            "AND ( " +
            "  (p.payment_method = 'COD' AND p.status = 'PENDING') " +
            "  OR " +
            "  (p.payment_method != 'COD' AND p.status = 'SUCCESS') " +
            ")",
            nativeQuery = true)
    Long countPendingOrdersWithValidPayment();





}
