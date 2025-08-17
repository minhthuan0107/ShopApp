package com.project.shopapp.services.admin.order;

import com.project.shopapp.commons.OrderStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Payment;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.PaymentRepository;
import com.project.shopapp.responses.customer.order.OrderResponse;
import com.project.shopapp.responses.customer.orderdetail.OrderDetailResponse;
import com.project.shopapp.responses.customer.payment.PaymentResponse;
import com.project.shopapp.services.admin.shipping.GHTKService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderAdminService implements IOrderAdminService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private GHTKService ghtkService;

    @Override
    public Page<OrderResponse> getAllOrders(PageRequest pageRequest, String keyword) {
        Page<Order> orderPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            //Nếu keyword rỗng thì lấy danh sách
            orderPage = orderRepository.findAll(pageRequest);
        } else {
            //Nếu có keyword thì tìm theo keyword
            orderPage = orderRepository.searchOrdersByKeyword(keyword, pageRequest);
        }
        return orderPage.map(order -> {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
            //Chuyển đổi order-detail về order-detail response
            List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .collect(Collectors.toList());
            Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
            //Chuyển đổi payment về payment-response
            PaymentResponse paymentResponse = payment != null ? PaymentResponse.fromPayment(payment) : null;
            //Chuyển từ Order -> OrderResponse
            OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
            orderResponse.setOrderDetailResponses(orderDetailResponses);
            orderResponse.setPaymentResponse(paymentResponse);
            return orderResponse;
        });
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId) throws Exception {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderId)));
        String currentStatus = existingOrder.getOrderStatus();
        if (currentStatus.equals(OrderStatus.COMPLETED) || currentStatus.equals(OrderStatus.CANCELLED)) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(
                    MessageKeys.ORDER_CANNOT_BE_UPDATED));
        }
        String nextStatus = getNextAllowedStatus(currentStatus);
        /*
        if(nextStatus.equals("SHIPPING")){
            ghtkService.createOrderGHTK(existingOrder);
        }
         */
        existingOrder.setOrderStatus(nextStatus);
        orderRepository.save(existingOrder);
    }

    private String getNextAllowedStatus(String currentStatus) {
        switch (currentStatus) {
            case "PENDING":
                return "PROCESSING";
            case "PROCESSING":
                return "SHIPPING";
            case "SHIPPING":
                return "COMPLETED";
            default:
                return null;
        }
    }


}
