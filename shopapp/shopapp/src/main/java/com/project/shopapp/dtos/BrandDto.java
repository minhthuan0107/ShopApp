package com.project.shopapp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {
    @NotBlank(message = "Tên hãng sản phẩm không được để trống")
    private String name;
}
