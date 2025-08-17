package com.project.shopapp.responses.customer.order;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.responses.customer.orderdetail.OrderDetailResponse;
import com.project.shopapp.responses.customer.payment.PaymentResponse;
import lombok.*;

import java.time.LocalDate;
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
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("user_id")
    private Long userId;
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
    private boolean active;
    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetailResponses;
    @JsonProperty("payment")
    private PaymentResponse paymentResponse;
}
