package com.project.shopapp.services.customer.orderdetail;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.customer.orderdetail.OrderDetailResponse;
import com.project.shopapp.ultis.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    private final LocalizationUtils localizationUtils;
    @Override
    public OrderDetailResponse getOrderDetailById(Long orderDetailId) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(()-> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDERDETAIL_NOT_FOUND, orderDetailId)));
        OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail,OrderDetailResponse.class);
        return orderDetailResponse;
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsByOrderId(Long orderId) throws Exception {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND,  orderId)));
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(existingOrder);
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(orderDetail -> modelMapper.map(orderDetail, OrderDetailResponse.class))
                .collect(Collectors.toList());
        return orderDetailResponses;
    }

    @Override
    public List<OrderDetailResponse> getAllOrderDetails() {
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(orderdetailresponse -> modelMapper.map(orderdetailresponse,OrderDetailResponse.class))
                .collect(Collectors.toList());
        return orderDetailResponses;
    }
    @Override
    public void deleteOrderDetailById(Long orderDetailId) {
        Optional<OrderDetail> existingorderDetail = orderDetailRepository
                .findById(orderDetailId);
        existingorderDetail.ifPresent(orderDetail -> orderDetailRepository.delete(orderDetail));
    }
}

