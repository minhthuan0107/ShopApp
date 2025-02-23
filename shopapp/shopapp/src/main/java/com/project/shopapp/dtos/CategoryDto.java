package com.project.shopapp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    @NotBlank(message = "Name must not be empty")
    private String name;
}
