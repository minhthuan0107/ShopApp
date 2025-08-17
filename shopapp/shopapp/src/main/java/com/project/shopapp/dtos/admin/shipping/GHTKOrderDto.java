package com.project.shopapp.dtos.admin.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GHTKOrderDto {
    private List<ProductRequest> products;
    private OrderInfo order;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRequest {
        private String name;
        private double weight;
        private int quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderInfo {
        private String id;
        private String pick_name;
        private String pick_address;
        private String pick_province;
        private String pick_district;
        private String pick_ward;
        private String pick_tel;

        private String name;
        private String address;
        private String province;
        private String district;
        private String ward;
        private String tel;
        private String hamlet;
        private int pick_money;
        private String note;
        private int value;
    }
}
