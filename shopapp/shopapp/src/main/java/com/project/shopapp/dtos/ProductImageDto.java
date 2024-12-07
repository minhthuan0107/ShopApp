package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDto {
    @Min(value = 1,message = "Product's ID must be >0")
   @JsonProperty("product_id")
    private Long productId;
   @Size(min = 5, max = 200 , message = "Image's name")
    @JsonProperty("Image URL must be between 5 and 200 characters")
    private String 	imageUrl;
}
