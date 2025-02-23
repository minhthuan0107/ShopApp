package com.project.shopapp.services.order;

import com.project.shopapp.commons.OrderStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.response.order.OrderResponse;
import com.project.shopapp.services.order.IOrderService;
import com.project.shopapp.ultis.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Override
    public OrderResponse createOrder(OrderDto orderDto) throws Exception {
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, orderDto.getUserId()))
                );
        //convert orderDto => Order
        //Dùng thư viện Model Mapper
        modelMapper.typeMap(OrderDto.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDto, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setOrderStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDto.getShippingDate() == null ? LocalDate.now() : orderDto.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ORDER_INVALID_SHIPPING_DATE));
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        modelMapper.typeMap(Order.class, OrderResponse.class);
        OrderResponse orderResponse = new OrderResponse();
        modelMapper.map(order, orderResponse);
        return orderResponse;
    }

    @Override
    public OrderResponse getOrderById(long orderId) {
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
    public OrderResponse updateOrder(long orderId, OrderDto orderDto) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderId)));
        User existingUser = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, orderDto.getUserId())));
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
    public void deleteOrderById(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, id)));
            order.setActive(false);
            orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(long userId)  {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND,userId)));
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
        return orderResponses;
    }
}
