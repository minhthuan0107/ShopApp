package com.project.shopapp.response.order;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.dtos.PaymentDto;
import com.project.shopapp.models.Order;
import com.project.shopapp.response.orderdetail.OrderDetailResponse;
import com.project.shopapp.response.payment.PaymentResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderResponse  {
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("order_date")
    private Date orderDate;
    private String status;
    @JsonProperty("total_price")
    private Float totalPrice;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_date")
    private LocalDate shippingDate;
    @JsonProperty("tracking_number")
    private String trackingNumber;
    private boolean active;
    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetailResponses;
    @JsonProperty("payment")
    private PaymentResponse paymentResponse;
}
