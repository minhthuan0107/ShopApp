package com.project.shopapp.responses.favorite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Favorite;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteResponse {
    @JsonProperty("favorite_id")
    private Long favoriteId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_price")
    private BigDecimal productPrice;
    @JsonProperty("product_image")
    private String productImage;
    public static FavoriteResponse fromFavorite (Favorite favorite){
        FavoriteResponse favoriteResponse = FavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .userId(favorite.getUser().getId())
                .productId(favorite.getProduct().getId())
                .productName(favorite.getProduct().getName())
                .productPrice(favorite.getProduct().getPrice())
                .productImage(favorite.getProduct().getUrlImage())
                .build();
        return favoriteResponse;
    }



}
