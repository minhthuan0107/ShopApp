package com.project.shopapp.repositories;

import com.project.shopapp.models.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    //Xóa order và payment giá trị pending tránh spam order
    @Modifying
    @Transactional
    @Query(value = """
                DELETE o FROM orders o
                JOIN payments p ON o.id = p.order_id
                WHERE o.status = 'PENDING'
                AND p.payment_method IN :paymentMethods
                AND p.status = 'PENDING'
            """, nativeQuery = true)
    int deletePendingOnlineOrders(@Param("paymentMethods") List<String> paymentMethods);


}
