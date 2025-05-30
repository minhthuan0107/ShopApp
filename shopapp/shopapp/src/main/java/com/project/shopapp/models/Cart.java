package com.project.shopapp.models;

import com.project.shopapp.commons.CartStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "carts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private BigDecimal totalPrice = BigDecimal.ZERO; // Tổng giá trị giỏ hàng
    @Column(nullable = false)
    private Integer totalQuantity = 0; // Tổng số lượng sản phẩm
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartStatus status ;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private boolean active;
}
