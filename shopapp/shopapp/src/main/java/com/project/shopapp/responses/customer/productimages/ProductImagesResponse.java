package com.project.shopapp.responses.customer.productimages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImagesResponse {
    private Long id;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("url_image")
    private String urlImage;
}
