package com.project.shopapp.response.rate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.BaseEntity;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Rate;
import com.project.shopapp.response.orderdetail.OrderDetailResponse;
import lombok.*;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingResponse extends BaseEntity {
    @JsonProperty("rate_id")
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("comment_id")
    private Long commentId;
    private int rating;
    public static RatingResponse fromRate (Rate rate){
        RatingResponse ratingResponse = RatingResponse.builder()
                .id(rate.getId())
                .userId(rate.getUser().getId())
                .productId(rate.getProduct().getId())
                .commentId(rate.getComment().getId())
                .rating(rate.getRating())
                .build();
        ratingResponse.setCreateAt(rate.getCreateAt());
        ratingResponse.setUpdateAt(rate.getUpdateAt());
        return ratingResponse;
    }
}
