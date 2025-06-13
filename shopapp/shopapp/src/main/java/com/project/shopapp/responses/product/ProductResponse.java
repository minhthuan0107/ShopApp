package com.project.shopapp.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.BaseResponseEntity;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponseEntity {
    private Long id;
    private String name;
    private BigDecimal price;
    @JsonProperty("url_image")
    private String urlImage;
    private String description;
    private int quantity;
    private int sold;
    private Category category;
    private Brand brand;
    @JsonProperty("total_reviews")
    private Long totalReviews;
    @JsonProperty("average_rating")
    private Double averageRating;
    public static  ProductResponse fromProduct (Product  product){
        ProductResponse productResponse = ProductResponse.builder()
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
