package com.project.shopapp.services.customer.order;

import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.commons.OrderStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.orderdetail.OrderDetailDto;
import com.project.shopapp.dtos.customer.order.OrderDto;
import com.project.shopapp.dtos.customer.payment.PaymentDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.customer.order.OrderResponse;
import com.project.shopapp.responses.customer.orderdetail.OrderDetailResponse;
import com.project.shopapp.responses.customer.payment.PaymentResponse;
import com.project.shopapp.services.customer.cart.CartService;
import com.project.shopapp.services.admin.coupon.CouponAdminService;
import com.project.shopapp.services.customer.email.OrderMailService;
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
import java.time.LocalDateTime;
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
    private final CouponAdminService couponService;
    private final ShippingInfoRepository shippingInfoRepository;
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

        //Tạo thông tin vận chuyển đơn hàng
        ShippingInfo shippingInfo = createShippingInfo(order, orderDto);
        shippingInfoRepository.save(shippingInfo);

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
        // Chỉ thực hiện khi người dùng thực sự nhập mã hợp lệ (không rỗng, không khoảng trắng)
        if (StringUtils.hasText(orderDto.getCouponCode())) {
            try {
                // Kiểm tra mã hợp lệ & quyền sử dụng
                couponService.checkCoupon(user.getId(), orderDto.getCouponCode());
                Coupon coupon = couponRepository.findByCode(orderDto.getCouponCode())
                        .orElseThrow(() -> new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND)));
                // Nếu là PUBLIC => không tạo UserCoupon
                if (!coupon.isPublic()) {
                    UserCoupon userCoupon = userCouponRepository
                            .findByUserIdAndCouponId(user.getId(), coupon.getId())
                            .orElseThrow(() -> new DataNotFoundException(
                                    localizationUtils.getLocalizedMessage(MessageKeys.COUPON_ACCESS_DENIED)));
                    ;
                    userCoupon.setOrder(order);
                    userCoupon.setAppliedAt(LocalDate.now());
                    if ("Cod".equals(orderDto.getPayment().getPaymentMethod())) {
                        userCoupon.setStatus(CouponStatus.USED);
                    }
                    userCouponRepository.save(userCoupon);
                }
                applyDiscount(order, coupon);
                orderRepository.save(order);
            } catch (Exception e) {
                log.warn(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_INVALID));
            }
        }
    }

    private void applyDiscount(Order order, Coupon coupon) {
        order.setTotalPrice(order.getTotalPrice().subtract(coupon.getValue()));
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

    private ShippingInfo createShippingInfo(Order order, OrderDto orderDto) {
        return ShippingInfo.builder()
                .order(order)
                .province(orderDto.getProvince())
                .district(orderDto.getDistrict())
                .ward(orderDto.getWard())
                .address(orderDto.getAddressDetail())
                .shippingMethod(orderDto.getShippingMethod())
                .shippingDate(null)
                .trackingNumber(null)
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
        modelMapper.typeMap(Order.class, OrderResponse.class)
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), OrderResponse::setUserId));
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
