package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name ="order_id ")
    private Order order;
    @ManyToOne
    @JoinColumn(name ="product_id  ")
    private Product product;
    @Column(name = "unit_price",nullable = false)
    private BigDecimal unitPrice;
    @Column(name = "quantity",nullable = false)
    private int quantity;
    @Column(name = "total_price",nullable = false)
    private BigDecimal totalPrice;





}
