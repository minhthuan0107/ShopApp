package com.project.shopapp.models;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Data
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    private BigDecimal amount;
    @Column(name = "payment_method", length = 20)
    private String paymentMethod;
    @Column(name = "transaction_id", length = 50)
    private String transactionId;
    @Column(name = "status", length = 20)
    private String status;


}
