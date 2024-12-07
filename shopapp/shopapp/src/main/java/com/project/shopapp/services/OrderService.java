package com.project.shopapp.services;

import com.project.shopapp.commons.OrderStatus;
import com.project.shopapp.dtos.OrderDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.response.OrderResponse;
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

    @Override
    public OrderResponse createOrder(OrderDto orderDto) throws Exception {
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDto.getUserId()));
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
            throw new DataNotFoundException("Date must be at least to day!");
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
    public OrderResponse getOrderById(long orderId) throws Exception {
        Order existingorder = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found with ID: " + orderId));
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
    public OrderResponse updateOrder(long orderId, OrderDto orderDto) throws Exception {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id:" + orderId));
        User existingUser = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id:" + orderDto.getUserId()));
        modelMapper.typeMap(OrderDto.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDto, existingOrder);
        existingOrder.setUser(existingUser);
        orderRepository.save(existingOrder);
        OrderResponse orderResponse = modelMapper.map(existingOrder, OrderResponse.class);
        return orderResponse;
    }

    @Override
    public void deleteOrderById(long id) {
        Order order = orderRepository.findById(id)
                .orElse(null);
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + userId));
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
        return orderResponses;
    }
}
