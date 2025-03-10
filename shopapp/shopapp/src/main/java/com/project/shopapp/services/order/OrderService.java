package com.project.shopapp.services.order;

import com.project.shopapp.commons.OrderStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDetailDto;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.response.order.OrderResponse;
import com.project.shopapp.response.orderdetail.OrderDetailResponse;
import com.project.shopapp.response.payment.PaymentResponse;
import com.project.shopapp.services.cart.CartService;
import com.project.shopapp.services.order.IOrderService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final LocalizationUtils localizationUtils;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, OrderDto orderDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId))
                );
        Order order = convertToOrder(orderDto, user);
        orderRepository.save(order);
        // Chuyển đổi danh sách OrderDetailDto -> OrderDetail
        List<OrderDetail> orderDetails = orderDto.getOrderDetails().stream()
                .map(orderDetailDto -> convertToOrderDetail(orderDetailDto, order))
                .collect(Collectors.toList());
        orderDetailRepository.saveAll(orderDetails);
        // Tạo Payment nếu có thông tin thanh toán
        Payment payment = Payment.builder()
                .order(order)
                .amount(orderDto.getPayment().getAmount())
                .paymentMethod(orderDto.getPayment().getPaymentMethod())
                .transactionId(orderDto.getPayment().getTransactionId())
                .status("PENDING")
                .build();
        paymentRepository.save(payment);

        // Chuyển đổi danh sách OrderDetail => OrderDetailResponse
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .collect(Collectors.toList());
        // Chỉ xóa giỏ hàng nếu thanh toán trực tiếp
        if ("Cod".equals(orderDto.getPayment().getPaymentMethod())) {
            cartService.clearCart(userId);
        }
        // Chuyển đổi Order sang OrderResponse để trả về
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        orderResponse.setOrderDetailResponses(orderDetailResponses);
        orderResponse.setPaymentResponse(PaymentResponse.fromPayment(payment));
        return orderResponse;
    }
    private Order convertToOrder(OrderDto orderDto, User user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(OrderDto.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = modelMapper.map(orderDto, Order.class);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setActive(true);

        return order;
    }
    private OrderDetail convertToOrderDetail(OrderDetailDto orderDetailDto, Order order) {
        Product product = productRepository.findById(orderDetailDto.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, orderDetailDto.getProductId()))
                );
        return OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(orderDetailDto.getQuantity())
                .unitPrice(orderDetailDto.getUnitPrice())
                .totalPrice(orderDetailDto.getTotalPrice())
                .build();
    }
    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order existingorder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderId)));
        OrderResponse orderResponse = modelMapper.map(existingorder, OrderResponse.class);
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
        return orderResponses;
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long userId, Long orderId, OrderDto orderDto) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId)));
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderId)));
        modelMapper.typeMap(OrderDto.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDto, existingOrder);
        existingOrder.setUser(existingUser);
        existingOrder.setActive(true);
        orderRepository.save(existingOrder);
        OrderResponse orderResponse = modelMapper.map(existingOrder, OrderResponse.class);
        return orderResponse;
    }

    @Override
    public void deleteOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderId)));
        order.setActive(false);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId)));
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
        return orderResponses;
    }

    @Override
    @Transactional
    public int deletePendingOnlineOrders() {
        return orderRepository.deletePendingOnlineOrders(List.of("Vnpay", "Paypal", "Momo"));
    }
}
