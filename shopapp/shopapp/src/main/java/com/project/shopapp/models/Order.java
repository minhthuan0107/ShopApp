package com.project.shopapp.models;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;
    @Column(name = "fullname",length = 100)
    private String fullName;
    @Column(name = "email",length = 100)
    private String email;
    @Column(name = "phone_number",length = 20,nullable = false)
    private String phoneNumber;
    @Column(name = "address",length = 200)
    private String address;
    @Column(name = "note",length = 100)
    private String note;
    @Column(name = "order_date")
    private Date orderDate;
    @Column(name="status")
    private String orderStatus;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Column(name = "is_buy_now")
    private boolean isBuyNow;
    @Column(name = "active")
    private boolean active;
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShippingInfo shippingInfo;

}
