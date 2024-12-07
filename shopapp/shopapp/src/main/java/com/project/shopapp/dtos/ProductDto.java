package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    @NotBlank(message = "Titlle is required")
    @Size(min = 3, max =200,message = "Tittle must be between 3 and 200 characters")
    private String name;
    @Min(value = 0,message = "Price must be greater than or equal to 0")
    @Max(value = 1000000, message = "Price must be less than or equal to 10,000,000")
    private Float price;
    @JsonProperty("url_image")
    private String urlImage;
    private String description;
    @JsonProperty("category_id")
    private Long categoryId;
}
