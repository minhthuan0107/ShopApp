package com.project.shopapp.services.orderdetail;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDetailDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.response.orderdetail.OrderDetailResponse;
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
    public OrderDetailResponse createOrderDetail(OrderDetailDto orderDetailDto) throws Exception {
        Order order = orderRepository.findById(orderDetailDto.getOderId())
                .orElseThrow(() -> new RuntimeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderDetailDto.getOderId())));
        Product product = productRepository.findById(orderDetailDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND,orderDetailDto.getProductId())));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDto.getPrice())
                .numberOfProduct(orderDetailDto.getNumberOfProducts())
                .totalMoney(orderDetailDto.getTotalMoney())
                .color(orderDetailDto.getColor())
                .build();
        orderDetailRepository.save(orderDetail);
        OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail,OrderDetailResponse.class);
        return orderDetailResponse;
    }

    @Override
    public OrderDetailResponse getOrderDetailById(long orderDetailId) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(()-> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDERDETAIL_NOT_FOUND, orderDetailId)));
        OrderDetailResponse orderDetailResponse = modelMapper.map(orderDetail,OrderDetailResponse.class);
        return orderDetailResponse;
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsByOrderId(long orderId) throws Exception {
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
    public OrderDetailResponse updateOrderDetail(long orderDetailId, OrderDetailDto orderDetailDto) throws Exception {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDERDETAIL_NOT_FOUND, orderDetailId)));
        Order existingOrder = orderRepository.findById(orderDetailDto.getOderId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderDetailDto.getOderId())));
        Product existingProduct = productRepository.findById(orderDetailDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND,orderDetailDto.getProductId())));
        existingOrderDetail.setPrice(orderDetailDto.getPrice());
        existingOrderDetail.setNumberOfProduct(orderDetailDto.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDto.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDto.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        orderDetailRepository.save(existingOrderDetail);
        OrderDetailResponse orderDetailResponse = modelMapper.map(existingOrderDetail,OrderDetailResponse.class);
        return orderDetailResponse;
    }

    @Override
    public void deleteOrderDetailById(long orderDetailId) {
        Optional<OrderDetail> existingorderDetail = orderDetailRepository
                .findById(orderDetailId);
        existingorderDetail.ifPresent(orderDetail -> orderDetailRepository.delete(orderDetail));
    }
}

