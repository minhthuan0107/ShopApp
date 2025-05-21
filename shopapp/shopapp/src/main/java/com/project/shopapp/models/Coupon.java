package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String type; // "fixed" hoặc "percent"
    @Column(nullable = false)
    private BigDecimal value;
    @Column(name = "min_order_value")
    private BigDecimal minOrderValue;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    @Column(name = "is_active")
    private boolean isActive;
}
