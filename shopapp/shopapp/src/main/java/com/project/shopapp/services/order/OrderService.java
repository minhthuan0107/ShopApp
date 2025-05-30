package com.project.shopapp.services.order;

import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.commons.OrderStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDetailDto;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.dtos.payment.PaymentDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.order.OrderResponse;
import com.project.shopapp.responses.orderdetail.OrderDetailResponse;
import com.project.shopapp.responses.payment.PaymentResponse;
import com.project.shopapp.services.cart.CartService;
import com.project.shopapp.services.coupon.CouponService;
import com.project.shopapp.services.email.OrderMailService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    private final OrderMailService orderMailService;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);


    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, OrderDto orderDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId))
                );
        Order order = convertToOrder(orderDto, user);
        // Chuyển đổi danh sách OrderDetailDto -> OrderDetail
        List<OrderDetail> orderDetails = orderDto.getOrderDetails().stream()
                .map(orderDetailDto -> convertToOrderDetail(orderDetailDto, order))
                .collect(Collectors.toList());
        //Hàm tính tổng tiền từ order-detail
        BigDecimal totalPrice = calculateTotalPrice(orderDetails);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        //Kiểm tra nếu người dùng nhập mã giảm giá thì
        applyCouponIfPresent(user, orderDto, order);

        orderDetailRepository.saveAll(orderDetails);
        // Tạo Payment nếu có thông tin thanh toán
        Payment payment = createPayment(order, orderDto.getPayment());
        paymentRepository.save(payment);

        // Chuyển đổi danh sách OrderDetail => OrderDetailResponse
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .collect(Collectors.toList());
        //Check gửi mail cho đơn hàng mua trực tiếp
        handleCartAndEmail(orderDto, userId, order);

        // Chuyển đổi Order sang OrderResponse để trả về
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        orderResponse.setOrderDetailResponses(orderDetailResponses);
        orderResponse.setPaymentResponse(PaymentResponse.fromPayment(payment));
        return orderResponse;
    }
    private BigDecimal calculateTotalPrice(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(OrderDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void applyCouponIfPresent(User user, OrderDto orderDto, Order order) {
        if (StringUtils.hasText(orderDto.getCouponCode())) {
            try {
                couponService.checkCoupon(user.getId(), orderDto.getCouponCode());
                Coupon coupon = couponRepository.findByCode(orderDto.getCouponCode()).get();

                UserCoupon userCoupon = new UserCoupon();
                userCoupon.setUser(user);
                userCoupon.setCoupon(coupon);
                userCoupon.setOrder(order);

                if ("Cod".equals(orderDto.getPayment().getPaymentMethod())) {
                    userCoupon.setStatus(CouponStatus.USED);
                    userCoupon.setAppliedAt(LocalDate.now());
                } else {
                    userCoupon.setStatus(CouponStatus.ACTIVE);
                }

                order.setTotalPrice(order.getTotalPrice().subtract(coupon.getValue()));
                orderRepository.save(order);
                userCouponRepository.save(userCoupon);

            } catch (Exception e) {
                log.warn(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_INVALID));
            }
        }
    }
    private Payment createPayment(Order order, PaymentDto paymentDto) {
        return Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .paymentMethod(paymentDto.getPaymentMethod())
                .transactionId(paymentDto.getTransactionId())
                .status("PENDING")
                .build();
    }
    private void handleCartAndEmail(OrderDto orderDto, Long userId, Order order) throws Exception {
        if (!orderDto.isBuyNow() && "Cod".equals(orderDto.getPayment().getPaymentMethod())) {
            cartService.clearCart(userId);
            orderMailService.sendMailOrder(order);
        } else if ("Cod".equals(orderDto.getPayment().getPaymentMethod()) && orderDto.isBuyNow()) {
            orderMailService.sendMailOrder(order);
        }
    }
    private Order convertToOrder(OrderDto orderDto, User user) {
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
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(orderDetailDto.getQuantity())))
                .build();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order existingorder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderId)));
        OrderResponse orderResponse = modelMapper.map(existingorder, OrderResponse.class);
        //Tìm kiếm payment theo orderId
        Optional<Payment> payment = paymentRepository.findByOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        // Chuyển đổi danh sách OrderDetail => OrderDetailResponse
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .collect(Collectors.toList());
        orderResponse.setOrderDetailResponses(orderDetailResponses);
        if (payment.isPresent()) {
            orderResponse.setPaymentResponse(PaymentResponse.fromPayment(payment.get()));
        }
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
    public List<OrderResponse> getOrdersByUserId(Long userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId));
        }
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> orderResponses = orders.stream().map(order -> {
                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    // Lấy payment theo orderId
                    Optional<Payment> payment = paymentRepository.findByOrderId(order.getId());
                    if (payment.isPresent()) {
                        orderResponse.setPaymentResponse(PaymentResponse.fromPayment(payment.get()));
                    }
                    // Lấy danh sách order detail theo orderId
                    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
                    List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                            .map(OrderDetailResponse::fromOrderDetail)
                            .collect(Collectors.toList());
                    orderResponse.setOrderDetailResponses(orderDetailResponses);
                    return orderResponse;
                })
                .collect(Collectors.toList());
        return orderResponses;
    }

    @Override
    @Transactional
    public int deletePendingOnlineOrders() {
        return orderRepository.deletePendingOnlineOrders(List.of("Vnpay", "Paypal", "Momo"));
    }
}
