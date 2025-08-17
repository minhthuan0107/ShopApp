package com.project.shopapp.services.admin.shipping;
import com.project.shopapp.dtos.admin.shipping.GHTKOrderDto;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Payment;
import com.project.shopapp.models.ShippingInfo;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GHTKService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;

    @Value("${ghtk.token}")
    private String ghtkToken;



    public void createOrderGHTK(Order order) {
        // Xây dựng request từ thông tin Order
        GHTKOrderDto request = GHTKOrderDto.builder()
                .products(buildProducts(order))
                .order(buildOrderInfo(order))
                .build();

        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", ghtkToken);
        HttpEntity<GHTKOrderDto> entity = new HttpEntity<>(request, headers);

        String url = "https://services.giaohangtietkiem.vn/services/shipment/order";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Đã tạo đơn GHTK thành công");
            } else {
                System.out.println("Tạo đơn GHTK thất bại: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Lỗi gọi GHTK: " + e.getMessage());
        }
    }
    private List<GHTKOrderDto.ProductRequest> buildProducts(Order order) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
        List<GHTKOrderDto.ProductRequest> products = orderDetails.stream()
                .map(od -> new GHTKOrderDto.ProductRequest(
                        od.getProduct().getName(),
                        0.5,
                        od.getQuantity()
                ))
                .collect(Collectors.toList());
        return products;
    }

    private GHTKOrderDto.OrderInfo buildOrderInfo(Order order) {
        boolean isPaid = paymentRepository.existsByOrderIdAndStatus(order.getId(), "SUCCESS");
        int pickMoney = isPaid ? 0 : order.getTotalPrice().intValue();
        ShippingInfo info = order.getShippingInfo();

        return GHTKOrderDto.OrderInfo.builder()
                .id(order.getId().toString())
                // Người gửi
                .pick_name("ThuanLe Shop")
                .pick_address("Thôn 3") // cần là địa chỉ hợp lệ
                .pick_province("Quảng Bình")
                .pick_district("Quảng Trạch")
                .pick_ward("Quảng Thạch")
                .pick_tel("0394948725")
                // Người nhận
                .name(order.getFullName())
                .address(info.getAddress())  // cần chứa thông tin rõ ràng (thôn/xóm/đường)
                .province(info.getProvince())
                .district(info.getDistrict())
                .ward(info.getWard())
                .tel(order.getPhoneNumber())
                // Thêm thông tin khác
                .hamlet("Khác")  // GHTK bắt buộc có
                .pick_money(pickMoney)
                .note("Giao hàng tiết kiệm")
                .value(order.getTotalPrice().intValue())
                // Nếu bạn dùng xfast thì mở thêm 2 dòng này
                // .pick_option("cod")
                // .deliver_option("xteam")
                .build();
    }
}
