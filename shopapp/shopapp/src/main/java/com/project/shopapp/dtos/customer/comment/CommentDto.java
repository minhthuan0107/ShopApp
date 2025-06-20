package com.project.shopapp.dtos.customer.comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @NotNull(message = "Sản phẩm không được để trống")
    @JsonProperty("product_id")
    private Long productId;
    @NotBlank(message = "Nội dung bình luận không được để trống")
    private String content;
}
