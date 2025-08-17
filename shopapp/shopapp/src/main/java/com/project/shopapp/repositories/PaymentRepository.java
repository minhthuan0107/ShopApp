package com.project.shopapp.repositories;
import com.project.shopapp.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByTransactionId (String transactionId);

    Optional<Payment>  findByOrderId (Long orderId);
    boolean existsByOrderIdAndStatus(Long orderId, String paymentStatus);

    @Query(value = "SELECT order_id from  payments where transaction_id = :transactionId", nativeQuery = true)
    Optional<Long> findOrderIdByTransactionId(@Param("transactionId") String transactionId);
}
