package com.project.shopapp.responses.admin.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.BaseResponseEntity;
import com.project.shopapp.responses.product.ProductResponse;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductResponse  extends BaseResponseEntity {
    private Long id;
    private String name;
    private BigDecimal price;
    @JsonProperty("url_image")
    private String urlImage;
    private String description;
    private long quantity;
    private int sold;
    private Category category;
    private Brand brand;
    public static CreateProductResponse fromProduct (Product product){
        CreateProductResponse productResponse = CreateProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .urlImage(product.getUrlImage())
                .quantity(product.getQuantity())
                .sold(product.getSold())
                .description(product.getDescription())
                .category(product.getCategory())
                .brand(product.getBrand())
                .build();
        productResponse.setCreateAt(product.getCreateAt());
        productResponse.setUpdateAt(product.getUpdateAt());
        return  productResponse;
    }
}
