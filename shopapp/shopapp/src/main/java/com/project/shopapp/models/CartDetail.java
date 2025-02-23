package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false) // Khóa ngoại đến Cart
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Khóa ngoại đến Product
    private Product product;
    @Column(nullable = false)
    private Integer quantity = 1;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;
    // totalPrice được tính tự động, không cần set giá trị
    @Column(precision = 15, scale = 2)
    private BigDecimal totalPrice;
}
